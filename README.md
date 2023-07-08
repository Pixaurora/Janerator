# Janerator

A mod for using multiple types of generators in a single Minecraft world to have more varied terrain, such as flat and normal.

This is done by graphing an inequality on the horizontal plane (x and z coordinates) of the world and then using an alternate generator for where that inequality returns true.
By creating a more complex inequality, you can make a world take almost any shape, just like how the base game allows for endless creativity!

![A blue-green spiral generated using the mod](https://files.lostluma.net/Ot7vlS.png)

# Graphing Inequalities

Using an example inequality `x > z` will create its respective graph in-game:

![A world with a line dividing flat and normal terrain](https://files.lostluma.net/jmfa5l.png)

You can also define variables, which can then be used in a final inequality which is what decides where each type of generator is used.

For example, the first screenshot can be created using the following variables and inequality:

    variables: [
        "phi = (1 + sqrt(5)) / 2",
        "log_phi = ln(phi)",
        "dist_squared = x^2 + z^2",
        "angle = ln(dist_squared) / log_phi"
    ],
    inequality: "(z - x * tan(angle)) * sgn(tan(angle) * csc(angle)) > 0"

---

All math is parsed and evaluated using mXparser, so you can use its own docs where relevant to help you write equations.

## Built-in Functions and variables
* [Variables (ie. pi, e, etc.)](https://mathparser.org/mxparser-tutorial/built-in-constants/)
* [1 argument functions (ie. sin, tan, ln, etc.)](https://mathparser.org/mxparser-math-collection/unary-functions/)
* [2 argument functions (ie. mod, log(a,b) etc.)](https://mathparser.org/mxparser-math-collection/binary-functions/)
* [3 argument functions](https://mathparser.org/mxparser-math-collection/3-args-functions/)
* [Functions with an arbitrary amount of variables (ie. max)](https://mathparser.org/mxparser-math-collection/variadic-functions/)
