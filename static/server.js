const express = require('express')
const app = express()
const port = 3000
const path = require('path');
var ejs = require('ejs');
var bodyParser = require('body-parser');

app.use(bodyParser.urlencoded({extended: true}));
app.set('views', path.join(__dirname, 'templates'));
app.engine('html', require('ejs').renderFile);
app.set('view engine', 'html');
app.use(express.static('public'));
app.use(express.static('images'));
app.use(express.json());


// Render Html File
app.get('/', function(req, res) {
  res.sendFile(path.join(__dirname, 'templates/index.html'));
});


app.listen(port, () => {
  // Code.....
})
