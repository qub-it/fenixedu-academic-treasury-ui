function messageAlert(title, message)
{
	bootbox.dialog({
	  title: title,
	  message: message,
	  buttons: {
		    success: {
		      label: "Ok",
		      className: "btn-primary",
		    }
	}
});
}

window.jQueryClosures = []

function initSelect2(element_id, elements_data_source,  element_init_val )
{
	var func = function(){
		$(element_id).select2({data : elements_data_source}).select2('val',element_init_val);
	};
	console.log(func);
	console.log(window.jQueryClosures);
	window.jQueryClosures.push(func);
	console.log(window.jQueryClosures);
}



$(document).ready(function() {
	console.log(window.jQueryClosures);
	
	for (var i = 0; i < window.jQueryClosures.length; i++)
	{
		window.jQueryClosures[i].call();
	}
});

