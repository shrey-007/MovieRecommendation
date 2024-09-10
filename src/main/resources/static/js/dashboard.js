//this is for search bar,jaise hi user search bar mai kuch daalega toh ye function call hoga
const search=()=>{
  //search bar ek input element h html ka toh usme jo bhi value aa rhi hai voh fetch kri val() function se
  let query=$("#search-input").val();
  console.log(query);

  //agar vo query null hai toh kuch mt kro
  if(query==''){}
  else{
    console.log(query);

    //sending request to server
    let url=`http://localhost:8080/search/${query}`;

    fetch(url).then(response=>{
      return response.json();
    }).then(data=>{
      console.log(data);

      let text=`<div class='list-group'>`;
      data.forEach(movie=>{
        text+=`<a href='/movies/movie?title=${movie.title}' class='list-group-item list-group-action customList'>${movie.title}</a>`
      })
      text+=`</div>`;
      $(".search-result").html(text);
      $(".search-result").show();
    });

    //agar null nhi hai toh simply jo search-result vaala div hidden tha usko show krdo.
  }
}

// Function to hide search results
function hideResults() {
    const results = document.getElementById(".customList");
    results.style.display = "none"; // Hide the search results
}

// Add an event listener to detect clicks outside of the search container
document.addEventListener("click", function(event) {
    const searchContainer = document.querySelector(".search-container");
    if (!searchContainer.contains(event.target)) {
        hideResults(); // Hide the search results if the click is outside the search container
    }
});
// Prevent clicks inside the search container from hiding the results
document.querySelector(".search-container").addEventListener("click", function(event) {
    event.stopPropagation(); // Stop the event from propagating to the document
});