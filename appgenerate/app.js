// Other required packages
var createError = require('http-errors'); 
var cookieParser = require('cookie-parser'); 
var express = require('express'); 
var path = require('path'); 
var logger = require('morgan');
const bodyParser = require('body-parser');
const fs = require('fs');
const session = require('express-session'); 
const mongoose = require('mongoose'); 
const methodOverride = require('method-override'); 
const flash = require('connect-flash'); 

const app = express(); 
// Database connection
mongoose.connect('mongodb://localhost:27017/elearnhub');
const conn = mongoose.connection;

conn.on('connected', () => {
  console.log('MongoDB connected successfully');
});

conn.on('error', (err) => {
  console.log('MongoDB connection error: ', err);
});

app.use(session({
  resave:false,
  saveUninitialized:false,
  secret:"sakshitathe",
  cookie: { secure: false,maxAge: 600000 }
}))

// Middleware
app.use(logger('dev')); 
app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(cookieParser());
app.use(methodOverride('_method'));
app.use(flash());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

// Static files
app.use(express.static(path.join(__dirname, 'public')));

// Routes
const indexRouter = require('./routes/index');
app.use('/', indexRouter);
const videoRouter = require('./routes/videoss');
app.use('/', videoRouter);

app.use((req, res, next) => {
  res.locals.errorMessage = req.flash('error');
  next();
});

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

// Home Route
app.get('/', (req, res) => {
  res.render('index', { user: req.session.user });
  });

// Start Server
module.exports = app;
const PORT = process.env.PORT || 3030;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));

