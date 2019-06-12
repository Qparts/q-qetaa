/**
 * 
 */

function openOrderTab(tabName) {
	var i;
	var x = document.getElementsByClassName("order-tab");
	for (i = 0; i < x.length; i++) {
		x[i].style.display = "none";
	}
	document.getElementById(tabName).style.display = "block";
}

function handleDrop(event, ui) {
	var droppedCar = ui.draggable;
	droppedCar.fadeOut('fast');
}

function resetActive(percent, step) {
	$(".progress-bar").css("width", percent + "%").attr("aria-valuenow",
			percent);
	$(".progress-completed").text(percent + "%");
	$("div").each(function() {

	});

	hideSteps();
	showCurrentStepInfo(step);
}

function hideSteps() {
	$("div").each(function() {
		if ($(this).hasClass("activeStepInfo")) {
			$(this).removeClass("activeStepInfo");
			$(this).addClass("hiddenStepInfo");
		}
	});
}

function showCurrentStepInfo(step) {
	var id = "#" + step;
	$(id).addClass("activeStepInfo");
}
function showModal3(){
	$("#myModal3").modal();
}
$(document).ready(function(event) {
	$("#myBtn").click(function(event) {
		$("#myModal").modal();
	});

	$("#myBtn2").click(function(event) {
		$("#myModal2").modal();
	});

	$("#myBtn3").click(function() {
		$("#myModal3").modal();
	});
	$("#myBtn4").click(function() {
		doLogout();
	});
	$("#myBtn5").click(function() {
		$("#myModal3").modal("toggle");
		$("#myModal").modal();
	});
	$("#myBtn6").click(function() {
		$("#myModal3").modal("toggle");
		$("#myModal2").modal();
	});
});

function hideModalAndShowLogin() {
	$("#myModal3").modal("toggle");
	$("#myModal").modal();
}

function hideModalAndShowRegister() {
	$("#myModal3").modal("toggle");
	$("#myModal2").modal();
}

function showRegisterDialog() {
	$("#myModal2").modal();
}

function showCompleteDialog() {
	$("#completeModal").modal();
}

function hideModal3(){
	$("#myModal3").modal("toggle");
}

function showCartDialog() {
	$("#myModal3").modal();
}

var __dcid = __dcid || [];
__dcid.push([ "DigiCertClickID_Bs0_36Wa", "3", "s", "black", "Bs0_36Wa" ]);
(function() {
	var cid = document.createElement("script");
	cid.async = true;
	cid.src = "//seal.digicert.com/seals/cascade/seal.min.js";
	var s = document.getElementsByTagName("script");
	var ls = s[(s.length - 1)];
	ls.parentNode.insertBefore(cid, ls.nextSibling);
}());

function showDivAndHideOthers(elem) {
	if (elem.value == 'V') {
		document.getElementById('visa-div').style.display = "block";
		document.getElementById('mc-div').style.display = "none";
		document.getElementById('wire-div').style.display = "none";
	} else if (elem.value == 'M') {
		document.getElementById('visa-div').style.display = "none";
		document.getElementById('mc-div').style.display = "block";
		document.getElementById('wire-div').style.display = "none";
	} else if (elem.value == 'W') {
		document.getElementById('visa-div').style.display = "none";
		document.getElementById('mc-div').style.display = "none";
		document.getElementById('wire-div').style.display = "block";
	}
	else{
		document.getElementById('visa-div').style.display = "none";
		document.getElementById('mc-div').style.display = "none";
		document.getElementById('wire-div').style.display = "none";
	}

}


function parseArabic(event, val) {
	str = val.value;
	var num = changeArabic(str);
	var mobile = document.getElementById(val.id);
	mobile.value = "0"+num;	
}

function changeArabic(str){
	return Number(str.replace(/[٠١٢٣٤٥٦٧٨٩]/g, function(d) {
        return d.charCodeAt(0) - 1632; // Convert Arabic numbers
    }).replace(/[۰۱۲۳۴۵۶۷۸۹]/g, function(d) {
        return d.charCodeAt(0) - 1776; // Convert Persian numbers
    }) );
}


function handleFileSelect(fileInput, imageId, stringId, btnId) {
	var file = fileInput.files[0];
    var img = document.createElement("img");
    img.imageId = imageId;
    img.stringId = stringId;
    img.btnId = btnId;
    img.onload = resizeImage;
    img.src = (URL || webkitURL).createObjectURL(file);
}


function resizeImage(){
	var img = this;
	var canvas = document.createElement('canvas');
	var imgstring = document.getElementById(img.stringId);
	var width = img.width;
    var height = img.height;

    var max_width = 976;
    var max_height = 610;

    // calculate the width and height, constraining the proportions
    if (width > height) {
        if (width > max_width) {
            height = Math.round(height *= max_width / width);
            width = max_width;
        }
    } else {
        if (height > max_height) {
            width = Math.round(width *= max_height / height);
            height = max_height;
        }
    }
    
    if(height > width){
    	
    }
    
    // resize the canvas and draw the image data into it
    canvas.width = width;
    canvas.height = height;
    var ctx = canvas.getContext("2d");
    ctx.drawImage(img, 0, 0, width, height);
  //  ctx.rotate(degrees*Math.PI/90);
    
    // finally, update
    var converted = document.getElementById(img.imageId);
    converted.src = canvas.toDataURL("image/jpeg", 0.5);
    imgstring.value = converted.src;
    converted.style.display = "inline"; //show the hidden pic   
    converted.style.visibility = "visible";
    converted.style.width = "40%";      // [image].width is read-only
    (URL || webkitURL).revokeObjectURL(this.src);
    clickUpload(img.btnId);
}

function replaceButton(btn){
	var uploadButton = document.getElementById(btn);
	uploadButton.click();
}

function clickUpload(btnId){
	var bt = document.getElementById(btnId);
	bt.click();
}


function openQuotationTab(evt, tabName) {
	// Declare all variables
	var i, tabcontent, tablinks;

	// Get all elements with class="tabcontent" and hide them
	tabcontent = document.getElementsByClassName("q-tabcontent");
	for (i = 0; i < tabcontent.length; i++) {
		tabcontent[i].style.display = "none";
	}

	// Get all elements with class="tablinks" and remove the class "active"
	tablinks = document.getElementsByClassName("q-tablinks");
	for (i = 0; i < tablinks.length; i++) {
		tablinks[i].className = tablinks[i].className.replace(" active", "");
	}

	// Show the current tab, and add an "active" class to the button that opened the tab
	document.getElementById(tabName).style.display = "block";
	evt.currentTarget.className += " active";
}