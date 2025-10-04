// routes/auth.js 
var express = require('express');
var router = express.Router();
const app = express();
const passport = require('passport');
const path = require('path');
const multer = require('multer');
const mongoose = require('mongoose'); 

const fs = require('fs'); 
const User =require('../models/User');  // Import your Mongoose model
const Video = require('../models/Video'); // Register Video schema

/*
router.get('/printuse', async (req, res) => {
  try {
    const users = await User.find(); // Fetch all users from the database
    res.json(users); // Send the users as a JSON response
  } catch (err) {
    console.error('Error fetching users:', err);
    res.status(500).json({ error: 'Failed to fetch users' });
  }
});
*/

router.get('/save-json', async (req, res) => {
  console.log('Route /save-json hit');
  // Path to the JSON file
  const jsonFilePath = 'stddata.json';
  try {
    // Read the JSON file
    const jsonData = fs.readFileSync(jsonFilePath, 'utf-8');
    // Parse the JSON file content into JavaScript object
    const parsedData = JSON.parse(jsonData);
     // Ensure that parsedData is an array (if it's multiple users)
     if (Array.isArray(parsedData)) {
      // Loop through each user and insert them into the database
      for (let userData of parsedData) {
        // Ensure that required fields are present in the userData
        if (userData.userId && userData.password && userData.name && userData.role) {
          const newUser = new User(userData);
          await newUser.save();
          console.log('User saved:', userData);
        } else {
          console.error('Missing required fields for user:', userData);
        }
      }
      res.json({ message: 'JSON data saved successfully' });
    } else {
      res.status(400).json({ error: 'Invalid JSON format. Expected an array of users.' });
    }
  } catch (err) {
    console.error('Error saving data:', err);
    res.status(500).json({ error: 'Failed to save JSON data' });
  }
});


/* GET home page. */

router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express it is ready ' });
});

// Render Login Page
router.get('/login', (req, res) => {
  res.render('login',{ errorMessage: null });
});

// Handle Login
router.post('/login', async (req, res) => {
  console.log("go to login post")
  // same name from html
  const { userIdd: uId, passwordd } = req.body;// Rename userID to uID
  try {
    const user = await User.findOne({ userId:uId });
    if (user && user.password === passwordd) {
      console.log("password is matched")
      req.session.user = user; // Store user ID in session
      res.redirect('/front');
    } else {
      const errorMessage = 'Invalid credentials. Please try again.'; // Define errorMessage
      res.render('login', { errorMessage }); // Pass the errorMessage to your view
    }
  } catch (err) {
    console.error(err);
    res.status(500).send('Error logging in');
  }
});

router.get('/front', async (req, res) => {
  if (req.session.user) {
    const videos = await Video.find().populate('uploadedBy', 'profileImage name'); // Fetch all videos
    res.render('frontpage', { user: req.session.user, videos }); // Pass videos to the EJS template
  } else {
    res.redirect('/login');
  }
});



router.get('/faculty', async (req, res) => {
  if (req.session.user) {
    const profile = await User.find({
      role : "teacher",
      uploadedVideos: { $exists: true, $not: { $size: 0 }}
    });
    res.render('faculty', { user: req.session.user, profile }); // Pass videos to the EJS template
  } else {
    res.redirect('/login');
  }
});



/*
router.get('/front', (req, res) => {
  if (req.session.user) {
    res.render('frontpage', { user: req.session.user });
  } else {
    res.redirect('/login');
  }
});*/
/*
router.get('/profiles', async (req, res) => {
  try {
    const user = await User.findById(req.session.user)
      .populate('uploadedVideos') // Populates 'uploadedVideos' from the 'Video' model
      .exec();
    if (!user) {
      throw new Error('User not found');
    }
    if (user.role === 'student') {
      res.render('stduser', { user });
    } else if (user.role === 'teacher') {
      res.render('teacheruser', { user });
    } else {
      res.redirect('/'); // Handle unknown role, or you can show an error page
    }
  } catch (err) {
    console.error(err);
    res.redirect('/'); // Redirect to home or error page
  }
});
*/

router.get('/profiles', async (req, res) => {
  try {
    const user = await User.findById(req.session.user);
    if (user.role === 'student') {
      res.render('stduser', { user });
    } else if (user.role === 'teacher') {
      await user.populate('uploadedVideos');
      res.render('teacheruser', { user });
    }
  } catch (err) {
    console.error(err);
    res.redirect('/');
  }
});

module.exports = router;
