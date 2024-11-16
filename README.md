This is a fork from Hypixel Public API https://github.com/HypixelDev/PublicAPI

I am lazy too so dont have high expectations. If you want to contribute feel free to make a pull request.

I did everything I needed and not in the sake of completion. If you need sth you are not unlikely to need to implement it yourself. If you think it is beneficial to the public make a Pull Request and I will probably merge it in. This Project just removed a bit of the hastle needing to implement the Full Cache System.

I made everything mainly of how I needed it, while trying to make modifications possible by making key methods protected instead of private. (Other than the offical API)

This Repo also has Museum and Garden API support which the Public API is lacking as of typing this.

This Repo also has a cache built in. This means that if you request the same data twice in a short amount of time, it will return the cached data instead of making a new request. This is to align the requests to Hypixel API Developer Policies.

For more details check the Java Doc of the HypixelAPI class.

You can also customize the Cache in the detailed constructor of the HypixelAPI class.

Keep in mind that the examples are for the official Hypixel API and I did not update them. I did extremely minimal changes but they can be neglected.
