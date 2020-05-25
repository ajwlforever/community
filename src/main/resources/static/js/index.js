$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

	// post
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();
	console.log(title);
	console.log(content);

	$.post(
		CONTEXT_PATH +"/post/add",
		{"title":title,"content":content},
		function (data) {
			data = $.parseJSON(data);
			//提示框显示返回信息
			$("#hintBody").text(data.msg);
			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
				if(data.code == 0)
				{
					window.location.reload();
				}
			}, 2000);
		}

	);

}