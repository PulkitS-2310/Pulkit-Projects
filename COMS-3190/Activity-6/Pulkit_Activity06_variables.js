/* 
Pulkit Saxena
Feb 13, 2026
Activity06 - Variables
*/

console.log("---- I am in V A R I A B L E S ----")

console.log("Q1 ---------------")
var var1 = "Iowa";
console.log(var1);
var var1 = 124;
console.log(var1);
console.log("Yes, var actually  allows redeclaration")


console.log("Q2 ----------------")
let var2 = "Ames";
console.log(var2);
console.log("No, let does NOT allow redeclaration")


console.log("Q3 ----------------")
let var3 = "ISU";
console.log(var3);
var3 = 2023;
console.log(var3);
console.log("Valid let allows reassignment")


console.log("Q4 ----------------")
let var4;
console.log("Error: We have a missing initializer in const declaration")


console.log("Q5 ----------------")
const var6 = 3.1415;
console.log("Error: Assignment to a constant variable is wrong, so that's why we get an error when we try to reassign var6")


console.log("Q6 ----------------")
console.log("A variable name cannot have spaces")
console.log("A variable name can't start with a number")
console.log("Hyphen is interpreted as subtraction, so that's why JS thinks that we are talking ")


console.log("Q7 ----------------")
let mainCity = "DesMoines";
console.log("Error: JavaScript is actually case-sensitive (MainCity ≠ mainCity)")


console.log("Q8 ----------------")

if (5 === 5) {
    var var7 = 100;
}
console.log(var7);

if (5 === 5) {
    let var8 = 100;
}
console.log("Error: let has a block scope and var does not")
