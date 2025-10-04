// routes/profile.js
const express = require('express');
const router = express.Router();
const User = require('../models/User');

// Render Profile Page based on Role (Student/Teacher)
router.get('/profile/:userId', ensureAuthenticated, async (req, res) => {
  try {
    const user = await User.findById(req.params.userId).populate('uploadedVideos');
    if (user.role === 'student') {
      res.render('studentProfile', { user });
    } else if (user.role === 'teacher') {
      res.render('teacherProfile', { user });
    }
  } catch (err) {
    console.error(err);
    res.redirect('/');
  }
});

module.exports = router;
