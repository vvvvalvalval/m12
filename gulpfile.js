var gulp = require('gulp');
var less = require('gulp-less');
var sourcemaps = require('gulp-sourcemaps');

gulp.task('less-dev', function() {
  gulp.src('./src/m12/**/*.main.less')
  .pipe(sourcemaps.init())
  .pipe(less({
    filename: 'm12.css'
  }))
  .pipe(sourcemaps.write())
  .pipe(gulp.dest('resources/public/css/compiled'));
});

gulp.task('styles-dev', function(){
  gulp.start('less-dev');
  gulp.watch('./src/m12/**/*.less', ['less-dev']);
})

gulp.task('dev', function(){
  gulp.start('styles-dev');
})

gulp.task('build-prod', function(){
  // TODO actual build for prod with minification etc. (Val, 30 Oct 2016)
  gulp.start('less-dev');
})