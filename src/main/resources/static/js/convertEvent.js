function convertListener() {
	var curId1 = document.getElementById("cur1").value;
	var curId2 = document.getElementById("cur2").value;
	var sum = document.getElementById("sum").value;
	var result = document.getElementById("result");
		console.log("HERE");	
	if (!sum) {
		result.textContent = "Введите сумму";
	}
	else {
		
		$.ajax({
			type : "GET",
			url : "/result",
			data: {curId1, curId2, sum},
			success:function(r){
				result.innerHTML = r;
			}
		});
	}
}
		
var buttonConvert=document.getElementById("convert");
buttonConvert.addEventListener('click', convertListener, false);