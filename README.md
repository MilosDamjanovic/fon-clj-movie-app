# clj-movie-review-app

## Prerequisites

You will need [Leiningen][1] 1.7.0 or above installed.
You will need Docker installed to create the database.

[1]: https://github.com/technomancy/leiningen

## Project overview

- Login system authentication system with hashed passwords stored in a PostgreSQL database
- Token based authentication on frontend, stored in `localStorage` for persistence.
- Sign up system, allowing new users to register an account.
- Basic front-end UI using [Bulma](https://bulma.io/)
- Client side routing, and a Backend API.
- Backend API is in charge of handling a simple movie review application.
- Front-end state management using [re-frame](https://github.com/Day8/re-frame)

## Running the App


**About start application and path to database:**

> When starting the application we need to start the docker container image configured in the `docker-compose.yml`.

```sh
sudo docker-compose up
sudo docker exec -it clj_movies_db psql -U postgres
```

When the docker container has started we need to connect to our database ``` \c clj_movies_db ``` and when connected we have access to our database.
This is the configuration for our database postgres database connection.


```
(ns movies.db
...

(def config
  {:classname "org.postgresql.Driver"
   :subprotocol "postgresql"
   :subname "//localhost:5432/clj_movies"
   :user "postgres"
   :password "postgres"})
	    ...  
	)

```

**Some about work application:**

>This application is intended for keeping track of movie reviews. The user can login to the application, create/update/delete and read movies, add/update/delete and get movie directors and leave (insert/update/create/delete) movie reviews for different movies and movie directors. You can register your user account. Only logged in users can create/edit movies and authors and leave movie reviews.

> The database definition is inside the **movies.sql** file and the server application is started by running the command:

```sh
clj -A run
```

When `Build completes` *'Server started'*  message appears in the output terminal.
[http://localhost:4002/](http://localhost:4002/).

For creating the database schema ```(hugsql/def-db-fns "movies.sql")``` we need to run this command in order to create the respective tables **author, genres, moviereview, users , movie** tables.


>After start the application, we have some cards in *navbar:* **Directors, Manage movie reviews,Users, Movies**.

>In card **Directors** we perform basic CRUD operations on the table autor in database.
First:**INSERT** author -we have form for entry data about new author in database clj_movies_db in table autor.
Second:**SELECT** authors -display in the table all the authors from the database.
Also we perform **UPDATE**author - change data about choosen author from table and save new data about the author in the table and database.
Third:**DELETE** author -  we delete the chosen author from database.

>In card **Movies** we also perform basic CRUD operations on the table movie in database bibliteka.
>>We connect table **movie** with table **autor** with foreign key by **autor_id**.
In this menu card we can **INSERT** new movie where have form for entry data about new movie. In this form also we have to choose the name of the author for movie which needs to be insert, but just authors which exists in database. Then we have **SELECT** command which display in the table all the movies from the database which order by movie_name.
With **DELETE** command: we delete choose movie from database.
At the end with **UPDATE**: we modify the data about chosen movie from table and save new data about the movie chosen in the database.


>In card **Manage movie reviews**, the possibility of entering and editing the data of a **new movie review** .
>> We connect table **moviereview** to table **movie** by *movie_id* and join the table **author** by *author_id* to create a complex primary key for this table relationship. The user first sees a list of all movie reviews and he can choose to **insert** a new movie review or **update** an existing movie review. In addition to entering these data, the logged in user of the application enters review information about which movie was directed by author (this represents an **aggregation** between a *movie* and a particular *author*). It introduces the *date_of_review* when a user has left a review and rating. 

>>We have an auxiliary table called **genres** which is connected to the *movie* table since every movie has a belonging genre.

>We add new card **Users** where list all registered users. 
>> On sign up we **insert-user** into the database. 

>>All CRUD operation which describe are here. We have multiple routes do differentiate the resources. 


BACKEND

#### Directory structure

>> The business logic is separated into multiple files depending on the entity which they need to handle.

> author.clj Handles all operations regarding movie directors ``` get-authors, create-author, get-author-by-id, update-author, delete-author ```
> movies.clj Handles all operations regarding movie  ``` get-authors, create-movie, get-movie-by-id, update-movie, delete-movie ```
> moviereview.clj Handles all operations regarding movie directors ``` get-movie-reviews, create-movie-review, get-movie-review, update-movie-review, delete-movie-review ```
> genre.clj Handles all operations regarding movie directors ``` get-genres ```
> user.clj Handles all operations regarding the user login, user registration and fetching all of the users ``` get-all-users, get-user-by-payload, create-user ```

>> The *routes.clj* which 'serve' different resources all of them except *"/login"* and *"/register"* have jwt auth middleware.
>> The *core.clj* list all of the routes and there we define our ``` -main ``` function where we can *start* our server. We use reitit for routing and provide all of the routes as a map where we provide the access to our resources.  
>> The *handler.clj* is an auxiliary file which, with the *user.clj* and *util.clj* handles the jwt authentication. We encrypt the password with our "JWT_SECRET" and store the user with the hashed token.

>> FRONTEND

#### Directory structure

* [`/`](/../../): project config files
* [`resources/public/`](resources/public/): SPA root directory;
  - [`index.html`](resources/public/index.html): SPA home page
    - Dynamic SPA content rendered in the following `div`:
        ```html
        <div id="app"></div>
        ```
  - Generated directories and files
    - `js/compiled/`: compiled CLJS (`shadow-cljs`)
      - Not tracked in source control; see [`.gitignore`](.gitignore)
* [`src/cljs/movies/`]: SPA source files (ClojureScript, [re-frame](https://github.com/Day8/re-frame))
  - [`core.cljs`](src/cljs/movies/core.cljs): contains the SPA entry point, `init[]`

### Running the App

Start a temporary local web server, build the app with the `dev` profile, and serve the app,
browser test runner and karma test runner with hot reload:

```sh
npm install
npx shadow-cljs watch app
```

Please be patient; it may take over 20 seconds to see any output, and over 40 seconds to complete.

When `[:app] Build completed` appears in the output, browse to
[http://localhost:8280/](http://localhost:8280/).

[`shadow-cljs`](https://github.com/thheller/shadow-cljs) will automatically push ClojureScript code changes to your browser on save. 


>> Handling the network requests for fetching authors, movies, genres and moviereviews, user registration and login. On login we use interceptors to store user and user token.

```
(ns movies.events) 

```

>> The inital view of the application which lists all of the panels and displays them based on whether the user is authenticated (Movies, Directors, Manage movie reviews and Users). or Sign up if the user isn't registerd.

```
(ns movies.views) 

```

>> We create a generic ``` field-control [id label control form-name] ``` method which we use to construct different 'custom' form components. input-field, text-area, password-input, date-picker, number-input,


```
(ns movies.form) 

```

>> Routes for our SPA application. For routes we define pass a map to our routes atom and when navigated to some known route we render the appropriate panel if the user is authorized.

```
(ns movies.routes)

(def routes
  (atom
   ["/" {""       (get-initial-panel)
         "movies" {"/all" :movies-index
                   "/create" :create-movie
                   ["/" :id "/view"] :movie-view
                   ["/" :id "/edit"] :edit-movie}
         "reviews"  {"" :movie-review-index
                           "/create" :create-movie-review
                           ["/edit/authors/" :author_id "/movies/" :movie_id ""] :edit-movie-review}
         "register" {"" :create-user
                  ["/users" ] :register-index}
         "login" :login-index
         "authors" {"" :authors-index
                    "/create" :create-author
                    ["/" :id "/edit"]  :edit-author
                    ["/" :id "/view"]  :author-view}}]))

```


## License

Copyright ©Milos Damjanovic 2021