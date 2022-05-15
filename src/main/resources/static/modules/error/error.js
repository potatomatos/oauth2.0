define(["js/httpPrompt"],function(http_error_statues) {
	return function(errorCode) {
		var msg=http_error_statues[errorCode]
		$(".num").html(errorCode)
		$(".error").html(msg)
	}
});
