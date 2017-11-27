'use strict';

const gulp = require('gulp');
const sourcemaps = require("gulp-sourcemaps");
const babel = require("gulp-babel");
const rm = require( 'gulp-rm' );
const gutil = require('gulp-util');
const webpack = require('webpack-stream');
const webpackConfig = require('./webpack-config');

gulp.task('app', function() {
    return gulp.src('./javascript/**/*.js')
        .pipe(webpack(Object.assign(webpackConfig, {
            devtool: 'source-map',
            output: {
                filename: 'userprofile.js'
            }
        })))
        .pipe(gulp.dest('./resources/webapp/js/layout/userprofile'));
});

gulp.task('app:dev', function() {
    return gulp.src('./javascript/**/*.js')
        .pipe(webpack(Object.assign(webpackConfig, {
            devtool: 'source-map',
            output: {
                filename: 'userprofile.js'
            },
            plugins: []
        } )).on('error', gutil.log))
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