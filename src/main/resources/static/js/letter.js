$(function(){
	$("#sendBtn").click(send_letter);
	$(".close").click(delete_msg);
});

function send_letter() {
	$("#sendModal").modal("hide");

	// post
	var toname = $("#recipient-name").val();
	var content = $("#message-text").val();


	$.post(
		CONTEXT_PATH +"/letter/send",
		{"toName":toname,"content":content},
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

function delete_msg() {
	// TODO 删除数据
	$(this).parents(".media").remove();
}