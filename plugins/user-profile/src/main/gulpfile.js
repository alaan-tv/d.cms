'use strict';

const gulp = require('gulp');
const sass = require('gulp-sass');
const rm = require( 'gulp-rm' );
const gutil = require('gulp-util');
const webpackStream = require('webpack-stream');
const webpack = require('webpack');
const webpackConfig = require('./webpack-config');

gulp.task('app', function() {
    return gulp.src('./javascript/**/*.js')
        .pipe(webpackStream(webpackConfig.production))
        .on('error', function handleError(err) {
            gutil.log(err);
            this.emit('end'); // Recover from errors
        })
        .pipe(gulp.dest('./resources/webapp/js/layout/userprofile'));
});

gulp.task('app:dev', function() {
    return gulp.src('./javascript/**/*.js')
        .pipe(webpackStream(webpackConfig.development))
        .on('error', function handleError(err) {
            gutil.log(err);
            this.emit('end'); // Recover from errors
        })
        .pipe(gulp.dest('./resources/webapp/js/layout/userprofile'));
});


gulp.task('watch', ['clean', 'app:dev'] , function (cb) {
    gulp.watch(['./javascript/**/*'], ['app:dev']);
    cb();
    console.log(gutil.colors.blue.bold('Go ahead, we are watching you :)'));
});

gulp.task('clean', function () {
    return gulp.src([
        './resources/webapp/js/layout/userprofile/userprofile.js'
    ],{read: false}).pipe(rm());
});

gulp.task('default', ['clean', 'app']);