/*global jQuery */
jQuery(function($) {

	'use strict';

    var _uploadFiles = [];

	var UploadApp = {

		create: function() {

            $('#uploadFile').on('change', this.prepareUpload);

            $('#uploadButton').on('click', this.uploadAction);

        },

        prepareUpload: function(event) {
            _uploadFiles = event.target.files;
        },

		uploadAction: function() {

            var data = new FormData();
            $.each(_uploadFiles, function(key, value) {
                data.append(value.name, value);
            });

            $.ajax({
                type: 'POST',
                url: '//localhost:4567/upload',
                data: data,
                cache: false,
                dataType: 'json',
                contentType: false,
                processData: false
            }).success(function(msg) {
                console.info(JSON.stringify(msg));
            }).error(function(msg) {
                console.info(JSON.stringify(msg));
            });
		}
	};

	UploadApp.create();
});
