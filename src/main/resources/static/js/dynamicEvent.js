var chart;
function loadChart(data) {
	if (chart) chart.dispose();
		chart = anychart.line(data);
	chart.container('container');
	chart.draw();
	return chart;
}
		
function dynamicListener() {
	var curId = document.getElementById("cur1").value;
	var e=document.getElementById("period");
	var period = e.options[e.selectedIndex].text;
	var c = document.getElementById("cur1");
	var cur = c.options[c.selectedIndex].text;
	if (cur!="Российский рубль")
		document.getElementById("chartHeader").textContent=
		"Динамика (" + cur + ")";
	$.ajax({
		type : "GET",
		url : "/dynamic",
		data: {period, curId},
		success:function(data){
			loadChart(data);
		}
	});
}
var buttonDynamic=document.getElementById("dynamic");
buttonDynamic.addEventListener('click', dynamicListener, false);