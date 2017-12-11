'use strict';

const gulp = require('gulp');
const rm = require( 'gulp-rm' );
const gutil = require('gulp-util');
const webpackStream = require('webpack-stream');
const webpack = require('webpack');
const webpackConfig = require('./webpack.config');


gulp.task('app', function() {
    return gulp.src('./javascript/main.js')
        .pipe(webpackStream(webpackConfig.production, webpack))
        .on('error', function handleError(err) {
            gutil.log(err);
            this.emit('end'); // Recover from errors
        })
        .pipe(gulp.dest('../../../target/generated-resources/webapp'));
});

gulp.task('app:dev', function() {
    return gulp.src('./javascript/main.js')
        .pipe(webpackStream(webpackConfig.development, webpack))
        .on('error', function handleError(err) {
            gutil.log(err);
            this.emit('end'); // Recover from errors
        })
        .pipe(gulp.dest('../../../target/generated-resources/webapp'));
});


gulp.task('watch', ['clean', 'app:dev'] , function (cb) {
    gulp.watch(['./javascript/**/*', './scss/**/*', './public/**/*'], ['clean', 'app:dev']);
    cb();
    console.log(gutil.colors.blue.bold('Go ahead, we are watching you :)'));
});

gulp.task('clean', function () {
    return gulp.src([
        '../../../target/generated-resources/webapp/**/*'
    ],{read: false}).pipe(rm());
});

gulp.task('default', ['clean', 'app']);