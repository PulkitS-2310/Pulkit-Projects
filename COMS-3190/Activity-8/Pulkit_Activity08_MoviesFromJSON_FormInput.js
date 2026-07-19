function getInputValue() {

    fetch("./Pulkit_Activity08_MoviesFromJSON.json")
    .then(response => response.json())
    .then(myMovies => loadMovies(myMovies));

    let movieName = document.forms["my_form"]["inputMovieName"];
    let inputMovieName = movieName.value;

    function loadMovies(myMovies) {

        var mainContainer = document.getElementById("goodmovies");

        mainContainer.innerHTML = "";

        for (var i = 0; i < myMovies.movies.length; i++) {

            if (myMovies.movies[i].title === inputMovieName) {

                let title = myMovies.movies[i].title;
                let year = myMovies.movies[i].year;
                let url = myMovies.movies[i].url;

                let div = document.createElement("div");

                div.innerHTML = `
                    <h2>${title}</h2>
                    <p>Year: ${year}</p>
                    <img src="${url}" width="200">
                    <br><br>
                `;

                mainContainer.appendChild(div);
            }
        }
    }
}