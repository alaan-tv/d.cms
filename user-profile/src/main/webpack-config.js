var webpack = require('webpack-stream');
var webpackCore = require('webpack');
const config = {
    bail: true,
    devtool: 'source-map',
    module: {
        loaders: [
            {
                test: /.jsx?$/,
                loader: 'babel',
                exclude: /node_modules/,
                query: {
                    babelrc: false,
                    presets: ['react', 'es2015']
                }
            },
            {
                test: /\.scss$/,
                loaders: [ 'style', 'css', 'sass' ]
            }
        ]
    },
    plugins: [
        new webpackCore.DefinePlugin({
            'process.env': {
                NODE_ENV: JSON.stringify('production')
            }
        }),
        new webpackCore.optimize.OccurrenceOrderPlugin(),
        new webpackCore.optimize.UglifyJsPlugin({
            compress: {
                screw_ie8: true, // React doesn't support IE8
                warnings: false
            },
            mangle: {
                screw_ie8: true
            },
            output: {
                comments: false,
                screw_ie8: true
            }
        })
    ],
    node: {
        fs: 'empty',
        net: 'empty',
        tls: 'empty'
    }
};
module.exports = config;