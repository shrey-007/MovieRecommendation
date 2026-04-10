//this is for search bar - as soon as the user types in the search bar, this function is called
const search = () => {
    let query = $("#search-input").val();

    if (query === '') {
        $(".search-result").hide();
        return;
    }

    console.log(query);

    //sending request to server
    let url = `http://localhost:8080/search/${query}`;

    fetch(url)
        .then(response => response.json())
        .then(data => {
            console.log(data);

            let text = `<div class='list-group'>`;
            data.forEach(movie => {
                text += `<a href='/movies/movie?title=${movie.title}' class='list-group-item list-group-action customList'>${movie.title}</a>`;
            });
            text += `</div>`;
            $(".search-result").html(text);
            $(".search-result").show();
        });
};

// Add an event listener to detect clicks outside of the search container
document.addEventListener("click", function(event) {
    const searchContainer = document.querySelector(".search-container");
    if (searchContainer && !searchContainer.contains(event.target)) {
        // Hide the search results if the click is outside the search container
        const searchResult = document.querySelector(".search-result");
        if (searchResult) {
            searchResult.style.display = "none";
        }
    }
});

// Prevent clicks inside the search container from hiding the results
const searchContainer = document.querySelector(".search-container");
if (searchContainer) {
    searchContainer.addEventListener("click", function(event) {
        event.stopPropagation();
    });
}