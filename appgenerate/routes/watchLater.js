// routes/watchLater.js
const express = require('express');
const router = express.Router();
const User = require('../models/User');

// Display user's watch later list
router.get('/watchlater', ensureAuthenticated, async (req, res) => {
  try {
    const user = await User.findById(req.user._id).populate('watchLater');
    res.render('watchLater', { videos: user.watchLater });
  } catch (err) {
    console.error(err);
    res.redirect('/');
  }
});

module.exports = router;
