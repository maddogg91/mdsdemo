const express = require('express')
const app = express()

const path = require('path');
var ejs = require('ejs');
var bodyParser = require('body-parser');
var fs = require('fs');
var redis= require('redis');
var unirest= require('unirest');
var {Client}= require('@googlemaps/google-maps-services-js');


app.set("views", path.join(__dirname, "templates"));
app.engine("html", require("ejs").renderFile);
app.set("view engine", "html");
app.use(express.static("public"));
app.use(express.static("images"));
app.use(express.json());
app.use(bodyParser.urlencoded({extended: true}));

const parent_dir= __dirname;
var calendar= require('./calendar-demo/index.js');


const c= new Client({});
var reslt= {}


app.get('/resources', function(req, res) {
res.render(path.join(parent_dir, 'resource-demo/templates/index.html'));

});

app.get('/results', function(req,res){
res.render(path.join(parent_dir, 'resource-demo/templates/map.html'));
});

app.post('/search', async function(req,res){
const body= req.body;
console.log(body);
const loc= `${body.lat}%2C${body.lon}`
const results= {"results" : await getData(loc, body.hidden)};
reslt= results;
fs.writeFile(path.join(parent_dir,'public/results.json'), JSON.stringify(results) ,err => {
	if (err) {
    console.error(err);
  } else {
	res.redirect('/results');
	}
	});


});

async function getData(loc, type){
  
  
const url= `https://maps.googleapis.com/maps/api/place/textsearch/json?location=${loc}&query=${type}&radius=1000&key=AIzaSyACT9OdzzHZUZtWN_Ug6yIpgTqRRmtmXUI`
 console.log(url);
 let header = {
        "User-Agent": "Mozilla/5.0 (Windows NT 6.3; Win64; x64)  AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.84 Safari/537.36 Viewer/96.9.4688.89"
    }
    return unirest.get(url).headers(header).then((response) => {
        return response.body.results;	
    })
}


// Render Html File
app.get('/', function(req, res) {
  res.sendFile(path.join(parent_dir, 'templates/index.html'));
});

const port = process.env.PORT || 8080;
app.listen(port, () => {
  console.log("Starting Server on port #: " + port)
});
