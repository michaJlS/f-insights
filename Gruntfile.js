module.exports = function(grunt) {

    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        bower: {
            install: {
                options: {
                    install: true,
                    copy: true,
                    targetDir: "./public/vendor",
                    cleanTargetDir: true,
                    cleanBowerDir: false,
                    layout: "byComponent"
                }
            }
        },
        handlebars: {
            options: {
                namespace: "FlickrAssistantTemplates",
                processName: function(filePath) {
                    return filePath.replace(/^app\/assets\/js\/templates\//, '').replace(/\.hbs$/, '');
                }

            },
            all: {
                files: {
                    "public/js/templates.js": ["app/assets/js/templates/*.hbs"]
                }
            }
        },
        watch: {
            handlebars: {
                files: ["app/assets/js/templates/*.hbs"],
                tasks: ["handlebars"]
            }
        }
    });

    grunt.loadNpmTasks("grunt-bower-task");
    grunt.loadNpmTasks("grunt-contrib-handlebars");
    grunt.loadNpmTasks("grunt-contrib-watch");
    grunt.registerTask("default", ["bower", "handlebars"]);

};