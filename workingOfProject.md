1. so when the user successfully logs in, defaultSuccessHandler in usermodule/config/SecurityConfig redirects user to ../dashboard
2. userModule deals with login,register,maintaining session and login and security configuration
3. StreamingModule is for streaming the video in chunks, it has just one VideoController
4. RabbitMQ module has some configuration related to RabbitMQ like queue name exchange name etc, and it has entities like producer, consumer and message(which goes to the queque). And it has one function to check whether the API request is successful or not(whether the consumer consumed the message and generated the recommendations or not)
5. moviesModule has a config, which is a configuration for restTemplate(to call python API)
6. moviesModule has these entities-: movie,userDashboard(maps userId to dashboard). It does not have User entity, voh userModule mai hai
7. moviesModule has a service package which just picks list of dashboard movies from dababase.
8. In moviesModule/MovieRecommender usme 2 functions hai, they both give 5 similar movies if a input movie is given. Difference is one is sync and another is async, code dono ka hi likha hai but use async hota hai.
   In moviesModule/UpdateDashBoard, it has dashboard2() function joki database se user ki watched movies uthaega, har ek ki 5 similar movies nikalega and then list return kr dega .
9. moviesModule/controller mai ek toh searchController hai jo search bar ke liye hai, ek MoviesController hai jisme movie2() gives 5 similar movies to a movie by calling moviesModule/MovieRecommender async method and another function is updateWatchListOfUser() which takes a movie and adds it to watch history of a user and calls the updateDashboard function to update the dashboard.
10. In UserController, dashboardFromDatabase() returns the dashboard of a user, if it is not present in database, then updateDashboard() is called to update the dashboard and return the new dashboard
11. In userModule there are 2 entities user and UserWatchesMovie(movies watched by user)
12. RabbitMQ has 3 entities producer,consumer,message

WorkFlow-:
1. User jaise hi login karega voh /dashboard pr jaaega jisse moviesModule/controller/UserController/ ka dashboardFromDatabase() function call hoga jo ki database se user dashboard nikaalege(UserDashboard is an entity saved in database). If the dashboard of the user is null then it calls moviesModule/helper/UpdateDashboard dashboard2() function and it does not saves the dashboard, it just returns it to dashboardFromDatabase() which return it to html file.

2. If a user click on a movie on a dashboard then it redirects it to movies/movie which maps to a controller moviesModule/controller/MovieController movie(), which calls the recommendMoviesAsync() function to get the recommendation of this movie.
   recommendMoviesAsync() creates a requestId and calls movieRecommendationProducer to get the the recommendation, and directly returns the requestId to the recommendMoviesAsync() which returns the requesId and some data related to movies to the webpage.
   Now webpage continuously checks whether the recommendation of the movie is ready or not, you can stream the movie, without even recommendations. Because our primary task is to stream movies and secondary is to show recommendations.
   Producer {requestId,movieId} queue mai daalega, consumer will take it and generate the list of similar movies and store it in cache(map)(requestID-> list of movies) and the moment this map contains the result, and frontend baar baar req. bhej rha hai ki uski req poori hui ki nhi , jaise hi map mai entry hogi us requestId ki toh voh return kr dega movies list.

3. If the user watches the movie 90%, then JS triggers a url movies/updateWatchList, and sends the title of the movies jisko user ne 90% dekh liya. request will go to the controller moviesModule/controller/HistoryController updateWatchListOfUser(). Ye Pehle toh database mai save karega ki uer ne ye vaali movie dekhi userWatchesMovie entity mai. Fir ye user dashboard update krne krega using moviesModule/helper/UpdateDashboard dashboard2(). It will save new dashboard of the user in the database