"use strict";

var MODAL_CONFIRM_TITLE = "확인";
var MODAL_CANCEL_TITLE = "취소";

var MODAL_BUTTON_OK = "확인";
var MODAL_BUTTON_CANCEL = "취소";

// JQuery UI 사용
function viewPromptModal(title, contents, callbackfunction) {
	fillModalData(title, contents);
	$("#dialog").dialog({
		resizable: false,
		height: "auto",
		width: 400,
		position: {
			of: $("body"),
			my: "center top",
			at: "center top+150"
		},
		modal: true,
		buttons: [ {
			text: MODAL_BUTTON_OK,
			click: function() {
				$(this).dialog("close");
				callbackfunction.call();
			}
		}, {
			text: MODAL_BUTTON_CANCEL,
			click: function() {
				$(this).dialog("close");
			}
		} ]
	});
}

// JQuery UI 사용
function viewConfirmModal(title, contents, callbackfunction) {
	fillModalData(title, contents);
	$("#dialog").dialog({
		resizable: false,
		height: "auto",
		width: 400,
		position: {
			of: $("body"),
			my: "center top",
			at: "center top+150"
		},
		modal: true,
		buttons: [ {
			text: MODAL_BUTTON_OK,
			click: function() {
				$(this).dialog("close");
				if (callbackfunction !== undefined && callbackfunction !== null) {
					callbackfunction.call();
				}
			}
		} ]
    });
}

function fillModalData(title, contents) {
	$("#dialog").attr("title", title);
	$("#dialog_contents").html(contents);
}

function callAjax(type, url, data, callbackfunction) {
	$.ajax({
		type: type,
		contentType: "application/json; charset=UTF-8",
		url: url,
		data: JSON.stringify(data),
		beforeSend: function() {
            $("#loading").css({
            	"display": "block",
                "width": $(document).width(),
                "height": $(document).height()
            });
        },
		success: function(data) {
			if (data.header === undefined) {
				viewConfirmModal(MODAL_CONFIRM_TITLE, MODAL_UNVALID_GRADE);
			}else if (data.header.resultCode === 200) {
				callbackfunction.call(this, data);
			} else {
				viewConfirmModal(MODAL_CONFIRM_TITLE, data.header.resultMessage);
			}
		},
		error: function(request, status, error){
			alert("code:" + request.status + "\nmessage:" + request.responseText + "\nerror:" + error);
        },
		complete: function() {
			$("#loading").css("display", "none");
		}
	});
}

// typeof(data) === FormData()
function callAjaxFormData(url, data, callbackfunction) {
	$.ajax({
		type: "POST",
		contentType: false,
		url: url,
		data: data,
		dataType: "json",
		processData: false,
		success: function(data) {
			if (data.header === undefined) {
				viewConfirmModal(MODAL_CONFIRM_TITLE, MODAL_UNVALID_GRADE);
			}else if (data.header.resultCode === 200) {
				callbackfunction.call(this, data.body);
			} else {
				viewConfirmModal(MODAL_CONFIRM_TITLE, data.header.resultMessage);
			}
		},
		error: function(request){
			viewConfirmModal(MODAL_CONFIRM_TITLE, MODAL_SERVER_CONNECT_ERROR);
        }
	});
}

function getParam(key) {
	var result;
	var keyValue = [];

	location.search.substr(1).split("&").forEach(function(item) {
		keyValue = item.split("=");

		if (keyValue[0] === key) {
			result = keyValue[1];
		}
	})

	if (result === undefined) {
		return result;
	} else {
		return decodeURIComponent(result);
	}
}

function defendXSS(str) {
	str = str.replace(/\</g, "&lt;");
	str = str.replace(/\>/g, "&gt;");
	str = str.replace(/\"/g, "&quot;");
	str = str.replace(/\'/g, "&#39;");

	return str;
}

function convertBr(str) {
	str = str.replace(/&lt;br\/&gt;/g, "<br/>");

	return str;
}

function isWhiteSpace(text) {
	if (text.trim() === "" | text == null) {
		return true;
	}

	return false;
}
