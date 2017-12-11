const webpack = require('webpack');
const path = require('path');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');

const extractCSS = new ExtractTextPlugin('[name].fonts.[hash].css', {publicPath: '/fe/'});
const extractSCSS = new ExtractTextPlugin('[name].styles.[hash].css', {publicPath: '/fe/'});


function config(DEBUG, VERBOSE) {

    const config = {
        context: path.resolve(__dirname, './javascript'),

        output: {
            sourcePrefix: '  ',
        },

        module: {
            rules: [
                {
                    test: /\.(js|jsx)$/,
                    exclude: /node_modules/,
                    use: {
                        loader: 'babel-loader',
                        options: {
                            cacheDirectory: DEBUG,
                            presets: ['react', 'env'],
                            plugins: ['transform-object-rest-spread']
                        }
                    }
                },
                {
                    test: /\.html$/,
                    loader: 'html-loader'
                },
                {
                    test: /\.(scss)$/,
                    use: ['css-hot-loader'].concat(extractSCSS.extract({
                        fallback: 'style-loader',
                        use: [
                            {
                                loader: 'css-loader',
                                options: {alias: {'../img': '../public/img'}}
                            },
                            {
                                loader: 'sass-loader'
                            }
                        ]
                    }))
                },
                {
                    test: /\.css$/,
                    use: extractCSS.extract({
                        fallback: 'style-loader',
                        use: 'css-loader'
                    })
                },
                {
                    test: /\.(png|jpg|jpeg|gif|ico)$/,
                    use: [
                        {
                            // loader: 'url-loader'
                            loader: 'file-loader',
                            options: {
                                name: './img/[name].[hash].[ext]'
                            }
                        }
                    ]
                },
                {
                    test: /\.(woff(2)?|ttf|eot|svg)(\?v=\d+\.\d+\.\d+)?$/,
                    loader: 'file-loader',
                    options: {
                        name: './fonts/[name].[hash].[ext]'
                    }
                }
            ]
        }
    };

    let PLUGINS = [
        new webpack.HotModuleReplacementPlugin(),
        new webpack.NamedModulesPlugin(),
        extractCSS,
        extractSCSS,
        new HtmlWebpackPlugin(
            {
                inject: true,
                template: '../public/index.html'
            }
        ),
        // Assign the module and chunk ids by occurrence count
        // Consistent ordering of modules required if using any hashing ([hash] or [chunkhash])
        // https://webpack.github.io/docs/list-of-plugins.html#occurrenceorderplugin
        new webpack.optimize.OccurrenceOrderPlugin(true),
    ];

    if( !DEBUG )
        PLUGINS.push(
            // Minimize all JavaScript output of chunks
            // https://github.com/mishoo/UglifyJS2#compressor-options
            new webpack.optimize.UglifyJsPlugin({
                compress: {
                    screw_ie8: true, // jscs:ignore requireCamelCaseOrUpperCaseIdentifiers
                    warnings: VERBOSE,
                },
            }),

            // A plugin for a more aggressive chunk merging strategy
            // https://webpack.github.io/docs/list-of-plugins.html#aggressivemergingplugin
            new webpack.optimize.AggressiveMergingPlugin()
        );

    const clientConfig = Object.assign(config, {
        entry: './main.js',

        output: {
            filename: '[name].[hash].js',
            chunkFilename: DEBUG ? '[name].[id].js?[chunkhash]' : '[name].[id].[chunkhash].js',
            publicPath: '/cms/fe/'
        },

        target: 'web',

        plugins: PLUGINS,

        // Choose a developer tool to enhance debugging
        // http://webpack.github.io/docs/configuration.html#devtool
        devtool: DEBUG ? 'source-map' : false,
    });


    return clientConfig;
}

module.exports = {
    production: config(false, false),
    development: config(true, true)
};
