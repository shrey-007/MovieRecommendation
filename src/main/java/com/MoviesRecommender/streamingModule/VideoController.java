package com.MoviesRecommender.streamingModule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/v1/videos")
@CrossOrigin("*")
@Slf4j
public class VideoController {

    // stream video in chunks
    // header mai range aa rhi hogi
    // basically browser 0 to end ki range ki request bhejega but apan code mai 0 to 1024 tak ki range bhejege browser ko
    // and browser usko play kr dega phir browser ko pata padega ki uske paas 1024 se end tak ka data nhi hai toh voh 1024 to end ka data ki request bhejega
    // but hum 1024 to 2048 ka data bhejege and so on
    @GetMapping("/stream/range")
    public ResponseEntity<Resource> streamVideoRange(@RequestHeader(value = "Range", required = false) String range) {

        Path path = Paths.get("C:\\Users\\shrey\\Desktop\\springboot\\MoviesRecommender\\src\\main\\resources\\videos\\sample.mp4");

        Resource resource = new FileSystemResource(path);

        //file ki length
        long fileLength = path.toFile().length();


        //pahle jaisa hi code hai kyuki range header null
        // agar range null hai toh poori video bhej do ek baar mai
        if (range == null) {
            return ResponseEntity.ok().contentType(MediaType.parseMediaType("video/mp4")).body(resource);
        }

        // if range header mai hai toh accordingly chunks bhejo

        //calculating start and end range

        long rangeStart;

        long rangeEnd;

        // start and end range dono parameter mai aayi hai, start vo hai jaha se browser ke paas data nhi hai
        // end is the end of video, but apan chahte hai ki end start+1024 ho toh end ko change kro
        String[] ranges = range.replace("bytes=", "").split("-");
        rangeStart = Long.parseLong(ranges[0]);

        rangeEnd = rangeStart + 1024*1024 - 1;  // end range , start range se 1 mb jyaada hoga

        // agar range , video se bahar ki hai toh use video ke last mai daaldo
        if (rangeEnd >= fileLength) {
            rangeEnd = fileLength - 1;
        }

        log.info("range start of video: {} ",rangeStart);
        log.info("range end of video: {} ",rangeEnd);
        InputStream inputStream;

        try {

            inputStream = Files.newInputStream(path);
            // stream ko startRange se chaalu kro, toh baaki ka purana part
            inputStream.skip(rangeStart);
            long contentLength = rangeEnd - rangeStart + 1;


            // ye hai vo byte array jo apan ko bhejna hai
            byte[] data = new byte[(int) contentLength];
            int read = inputStream.read(data, 0, data.length);
            log.info("read(number of bytes) : {} " , read);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Range", "bytes " + rangeStart + "-" + rangeEnd + "/" + fileLength);
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            headers.add("X-Content-Type-Options", "nosniff");
            headers.setContentLength(contentLength);

            return ResponseEntity
                    .status(HttpStatus.PARTIAL_CONTENT)
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("video/mp4"))
                    .body(new ByteArrayResource(data));


        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }


    }

}