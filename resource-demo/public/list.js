!function() {
	
	var today= moment();


}

function List(selector, events){
	const monthNames = ["JAN", "FEB", "MAR", "APR", "MAY", "JUN",
  "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"];
	this.el= document.querySelector(selector);
	var div= document.createElement('div');
	var ul = document.createElement('ul');
	div.classList.add("list-container");
	ul.classList.add("scale-up-hover-list");
	this.el.appendChild(div);
	events.forEach(function(item){
		//if(item.date > new Date()){
			var li = document.createElement('li');
			var a= document.createElement('a');
			a.setAttribute('href', "#");
	
			var date = document.createElement('div');
			var day = document.createElement('p');
			var time= document.createElement('small');
			day.classList.add("days");
			
			var d= new Date(item.date + " GMT-0400");
			
			day.innerText= d.getDate();
			
			time.innerText= "(" + item.start 
			+ "-" + item.end + ")";
			var month= document.createElement('p');
			month.classList.add("months");
			month.innerText= monthNames[d.getMonth()];
			date.appendChild(day);
			date.appendChild(month);
			date.classList.add("date");
			
			var info = document.createElement('div');
			var title= document.createElement('h1');
			var desc= document.createElement('p');
			info.classList.add('item-info-container');
			desc.classList.add('item-description');
			title.innerText= item.eventName;
			desc.innerText= item.desc + "\n\n" + item.address ;
			info.appendChild(time);
			info.appendChild(title);
			info.appendChild(desc);
			
			a.appendChild(date);
			a.appendChild(info);
			li.appendChild(a);
			ul.appendChild(li);
	}
//	else{
	//	var li = document.createElement('li');
		//	var a= document.createElement('a');
			//a.setAttribute('href', "#");
			
//			var info = document.createElement('div');
	//		var title= document.createElement('h1');
		//	var desc= document.createElement('p');
	//		info.classList.add('item-info-container');
		//	desc.classList.add('item-description');
		//	title.innerText= "There are no current events to display."
		//	desc.innerText= "Select Calendar view to display past events" ;
		//	a.appendChild(info);
		//	li.appendChild(a);
	//		ul.appendChild(li);
//	}
	//}
	);
		div.appendChild(ul);
		this.el.appendChild(div);
	

}

window.List= List;

!async function() {
  
	const e= await fetch('./events.json');

	const data= await e.json();
	
	const events_retrieved= data.data;
	
	var list = new List('#list', events_retrieved);

}();

