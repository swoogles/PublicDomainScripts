// I think fiddling with this file is the path towards clearing up the bajillion warnings now that I'm webpacking ZIO
module.exports = {
    "entry": {
        "example-fastopt": ["/Users/bill/Repositories/PublicDomainScripts/jsClient/scala-js-example-app/target/scala-2.12/scalajs-bundler/main/example-fastopt.js"]
    },
    "output": {
        "path": "/Users/bill/Repositories/PublicDomainScripts/jsClient/scala-js-example-app/target/scala-2.12/scalajs-bundler/main",
        "filename": "[name]-bundle.js"
    },
    "devtool": "source-map",
    "module": {
        "rules": [{
            "test": new RegExp("\\.js$"),
            "enforce": "pre",
            "use": ["source-map-loader"]
        }, {
            "enforce": "pre",
            "test": /\.js$/,
            "loader": "source-map-loader",
            "exclude": [/node_modules/, /build/, /__test__/, /__zio__/,
                "/Users/bill/Repositories/PublicDomainScripts/jsClient/scala-js-example-app/target/scala-2.12/scalajs-bundler/main/example-fastopt-bundle.js",
                "/Users/bill/Repositories/PublicDomainScripts/jsClient/scala-js-example-app/target/scala-2.12/scalajs-bundler/main/example-fastopt-bundle.js.map"]
        }]
    },
}
