fetch("./Pulkit_Activity08_MoviesFromJSON.json")
.then(response => response.json())
.then(myMovies => loadMovies(myMovies));

function loadMovies(myMovies) {

    let mainContainer = document.getElementById("goodmovies");

    for (let i = 0; i < myMovies.movies.length; i++) {

        let title = myMovies.movies[i].title;
        let year = myMovies.movies[i].year;
        let url = myMovies.movies[i].url;

        let col = document.createElement("div");
        col.className = "col-md-4 mb-4";

        col.innerHTML = `
            <div class="card h-100 shadow">
                <img src="${url}" class="card-img-top" style="height:300px; object-fit:contain;">
                <div class="card-body text-center">
                    <h5 class="card-title">${title}</h5>
                    <p class="card-text">Year: ${year}</p>
                </div>
            </div>
        `;

        mainContainer.appendChild(col);
    }
}