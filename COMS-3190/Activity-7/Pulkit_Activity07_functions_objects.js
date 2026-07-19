//Excercise 1
function maxOfTwo(n1, n2) {
    if (n1 > n2) {
        return n1;
    } else {
        return n2;
    }
}

let n1 = 11;
let n2 = 10;
console.log(`The max between ${n1} and ${n2} is :`, maxOfTwo(n1, n2));


//Excercise 2
function maxOfArray(array) {
    let maximum = array[0];

    for (let i = 1; i < array.length; i++) {
        if (array[i] > maximum) {
            maximum = array[i];
        }
    }

    return maximum;
}

let array = [10,11,1024,125,9,201];
console.log(maxOfArray(array));


//Excercise 3
function showProperties(movie) {
    console.log("List of Keys:");
    for (let key in movie) {
        console.log(key);
    }

    console.log("List of Values:");
    for (let key in movie) {
        console.log(movie[key]);
    }
}

// Object
const movie = {
    title: 'Some movie',
    releaseYear: 2018,
    rating: 4.5,
    director: 'Steven Spielberg'
};

showProperties(movie);


//Excercise 4
const circle = {
    radius: 2,
    area: function () {
        return Math.PI * this.radius * this.radius;
    }
};

console.log(circle.area());


//Excercise 5
const circle2 = {
    radius: 2,

    get radiusValue() {
        return this.radius;
    },

    set radiusValue(value) {
        this.radius = value;
    },

    area: function () {
        return Math.PI * this.radius * this.radius;
    }
};

console.log(`Area with ${circle2.radiusValue} :`, circle2.area());
circle2.radiusValue = 3;
console.log(`Area with ${circle2.radiusValue} :`, circle2.area());


//Excercise 6
const circle3 = {
    radius: 2,

    getRadiusValue: function () {
        return this.radius;
    },

    setRadiusValue: function (value) {
        this.radius = value;
    },

    area: function () {
        return Math.PI * this.radius * this.radius;
    }
};

console.log(`Area with ${circle3.getRadiusValue()} :`, circle3.area());
circle3.setRadiusValue(3);
console.log(`Area with ${circle3.getRadiusValue()} :`, circle3.area());


//Excercise 7
function calculateAverageGrade(grades) {
    let total = 0;
    let count = 0;

    for (let subject in grades) {
        total += grades[subject];
        count++;
    }

    return total / count;
}

const grades = {
    math: 85,
    science: 90,
    history: 75,
    literature: 88
};

console.log("Average Grade:", calculateAverageGrade(grades));


//Excercise 8

function calculateAverageGradeForStudents(array) {
    let result = {};

    for (let student of array) {
        for (let name in student) {
            let grades = student[name];
            let total = 0;
            let count = 0;

            for (let subject in grades) {
                total += grades[subject];
                count++;
            }

            result[name] = total / count;
        }
    }

    return result;
}

const students = [
    {
        Fer: {
            math: 85,
            science: 90,
            history: 75,
            literature: 88
        }
    },
    {
        Alex: {
            math: 99,
            science: 97,
            history: 94,
            literature: 90
        }
    },
    {
        Mary: {
            math: 79,
            science: 72,
            history: 81,
            literature: 79
        }
    }
];

console.log(calculateAverageGradeForStudents(students));
