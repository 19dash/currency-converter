$.ajax({
	type : "GET",
	url : "/banks",
	success:function(data){	
		var html;
		for ( var i = 0; i <= 2; i+=2) {
			html +=
			'<tr>'+
				'<td>'+ data[i].bank + '</td>' +
				'<td>'+ data[i].sell + '</td>' +
				'<td>'+ data[i].buy + '</td>' +
			'</tr>';
		}
		$('#table1').append(html);
		html='';
		for ( var i = 1; i <= 3; i+=2) {
			html +=
			'<tr>'+
				'<td>'+ data[i].bank + '</td>' +
				'<td>'+ data[i].sell + '</td>' +
				'<td>'+ data[i].buy + '</td>' +
			'</tr>';
		}
		$('#table2').append(html);
	}
});