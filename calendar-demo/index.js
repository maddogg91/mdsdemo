module.exports = function(app, path){
var fs = require('fs');
var redis= require('redis');
	
const loaded_events= load_events();


app.use(express.static("public"));
app.use(express.static("images")); 

	
const client = redis.createClient({
    password: process.env.REDIS,
    socket: {
        host: 'redis-16482.c1.us-east1-2.gce.redns.redis-cloud.com',
        port: 16482
    }
});

async function makeConnection(){
	await client.connect();
    const events= await client.json.get('events');
	
	fs.writeFile(path.join(__dirname,'public/events.json'), JSON.stringify(events) ,err => {
  if (err) {
    console.error(err);

  } else {
    console.log("Events loaded");
	
  }
});
}

app.post('/save', async function(req,res, next){
	new_event= req.body;
	if(new_event.calendar== 'Meeting'){
		new_event.color= "orange";
	}
	else if(new_event.calendar== 'Event'){
		new_event.color= "blue";
	}
	else{
		new_event.color= "green";
	}
	
	loaded_events.data.push(new_event);
	await client.json.set("events", '$' ,loaded_events);
	fs.writeFile(path.join(__dirname,'public/events.json'), JSON.stringify(loaded_events) ,err => {
	if (err) {
    console.error(err);
  } else {
    console.log("New event successfully written");
	res.redirect('/calendar');
	}
	});
});

function load_events(){
	return JSON.parse(fs.readFileSync(path.join(__dirname, 'public/events.json'), 'utf8'));
}

app.get('/calendar', async function(req,res, next){
res.render(path.join(__dirname, 'templates/index.html'), {events: load_events()});

});
}





