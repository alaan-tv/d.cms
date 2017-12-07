'use strict';

const gulp = require('gulp');
const sass = require('gulp-sass');
const rm = require( 'gulp-rm' );
const gutil = require('gulp-util');
const webpackStream = require('webpack-stream');
const webpack = require('webpack');
const webpackConfig = require('./webpack-config');

gulp.task('sass', function () {
    return gulp.src('./sass/**/*.scss')
        .pipe(sass.sync({outputStyle: 'compressed'}).on('error', sass.logError))
        .pipe(gulp.dest('../../../target/generated-resources/webapp/css/', {overwrite : true}));
});

gulp.task('sass:dev', function () {
    return gulp.src('./sass/**/*.scss')
        .pipe(sass.sync().on('error', sass.logError))
        .pipe(gulp.dest('../../../target/generated-resources/webapp/css/', {overwrite : true}));
});

gulp.task('app', function() {
    return gulp.src('./javascript/main.js')
        .pipe(webpackStream(webpackConfig.production, webpack))
        .on('error', function handleError(err) {
            gutil.log(err);
            this.emit('end'); // Recover from errors
        })
        .pipe(gulp.dest('../../../target/generated-resources/webapp/js'));
});

gulp.task('app:dev', function() {
    return gulp.src('./javascript/main.js')
        .pipe(webpackStream(webpackConfig.development, webpack))
        .on('error', function handleError(err) {
            gutil.log(err);
            this.emit('end'); // Recover from errors
        })
        .pipe(gulp.dest('../../../target/generated-resources/webapp/js'));
});


gulp.task('watch', ['clean', 'app:dev', 'sass:dev'] , function (cb) {
    gulp.watch(['./sass/**/*'], ['sass:dev']);
    gulp.watch(['./javascript/**/*'], ['app:dev']);
    cb();
    console.log(gutil.colors.blue.bold('Go ahead, we are watching you :)'));
});

gulp.task('clean', function () {
    return gulp.src([
        '.../../../target/generated-resources/webapp/css/**/*',
        '../../../target/generated-resources/webapp/js/main.js'
    ],{read: false}).pipe(rm());
});

gulp.task('default', ['clean', 'sass', 'app']);