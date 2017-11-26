'use strict';

const gulp = require('gulp');
const sass = require('gulp-sass');
const sourcemaps = require("gulp-sourcemaps");
const babel = require("gulp-babel");
const rm = require( 'gulp-rm' );
const gutil = require('gulp-util');
const webpack = require('webpack-stream');
const webpackConfig = require('./webpack-config');

gulp.task('sass', function () {
    return gulp.src('./sass/**/*.scss')
        .pipe(sass.sync({outputStyle: 'compressed'}).on('error', sass.logError))
        .pipe(gulp.dest('./resources/webapp/css/', {overwrite : true}));
});

gulp.task('sass:dev', function () {
    return gulp.src('./sass/**/*.scss')
        .pipe(sass.sync().on('error', sass.logError))
        .pipe(gulp.dest('./resources/webapp/css/', {overwrite : true}));
});

gulp.task('app', function() {
    return gulp.src('./javascript/**/*.js')
        .pipe(webpack(Object.assign(webpackConfig, {
            devtool: 'source-map',
            output: {
                filename: 'main.js'
            }
        })))
        .pipe(gulp.dest('./resources/webapp/js'));
});

gulp.task('app:dev', function() {
    return gulp.src('./javascript/**/*.js')
        .pipe(webpack(Object.assign(webpackConfig, {
            devtool: 'source-map',
            output: {
                filename: 'main.js'
            },
            plugins: []
        } )))
        .pipe(gulp.dest('./resources/webapp/js'));
});


gulp.task('watch', ['clean', 'app:dev', 'sass:dev'] , function (cb) {
    gulp.watch(['./sass/**/*'], ['sass:dev']);
    gulp.watch(['./javascript/**/*'], ['app:dev']);
    cb();
    console.log(gutil.colors.blue.bold('Go ahead, we are watching you :)'));
});

gulp.task('clean', function () {
    return gulp.src([
        './resources/webapp/css/**/*',
        './resources/webapp/js/main.js'
    ],{read: false}).pipe(rm());
});

gulp.task('default', ['clean', 'sass', 'app']);