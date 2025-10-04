const express = require('express');
const mongoose = require('mongoose');
const multer = require('multer');
const crypto = require('crypto');
const {GridFsStorage} = require('multer-gridfs-storage');
const Grid = require('gridfs-stream');
const GridFSBucket = require('mongodb').GridFSBucket;
const path = require('path');
const router = express.Router();
const User =require('../models/User');  // Import your Mongoose model
const Video =require('../models/Video'); // Register Video schema
const app = express();


router.get('/upload', (req, res) => {
  if (req.session.user) {
    console.log("go to upload page")
    res.render('upload', { user: req.session.user });
  } else {
    res.redirect('/login');
  }
});
// Mongo URI
const mongoURI = 'mongodb://localhost:27017/elearnhub';
 
// Create mongo connection
const conn = mongoose.createConnection(mongoURI);

// Init gfs
let gfs;

conn.once('open', () => {
  gfs = Grid(conn.db, mongoose.mongo);
  gfs.collection('videos');
  console.log("GridFS initialized");
});

 

// Create storage engine
const storage = new GridFsStorage({
  url: "mongodb://localhost:27017/elearnhub",
  file: (req, file) => {
    return new Promise((resolve, reject) => {
      crypto.randomBytes(16, (err, buf) => {
        if (err) {
          return reject(err);
        }
        const filename = buf.toString('hex') + path.extname(file.originalname);
        const fileInfo = {
          filename: filename,
          bucketName: 'videos'
        };
        resolve(fileInfo);
      });
    });
  }
});
const upload = multer({ storage });

router.post('/uploadd', upload.single('file'), async (req, res) => {
  console.log("go to file store ifa awwoonh")
  try {
    if (!req.file) {
      throw new Error('No file uploaded');
    }
    console.log("go to file store sucesss")
     // Add this to check the file details
    const user = await User.findById(req.session.user);
    //console.log(req.session);
    const newVideo = new Video({
      title: req.body.title,
      description: req.body.description,
      videoFile: req.file.filename,
      videoFileId: req.file.id,
      uploadedBy:user._id,  // Using the teacher's ID from req.user
      createdAt: Date.now(),
    });
    console.log("This file is uploaded sucessfully");
    const savedVideo = await newVideo.save();
    console.log("This file is in saved videos sucessfully");
    user.uploadedVideos.push(savedVideo._id);
    await user.save();
    console.log("User's uploadedVideos updated:", user.uploadedVideos);

    await user.save();

    console.log("Video uploaded successfully");
    res.render('teacheruser', { user });
  } catch (error) {
    res.status(500).send('Server Error');
  }
});

router.delete('/delete/:videoId', async (req, res) => {
  const videoId = req.params.videoId;
  const user = await User.findById(req.session.user);

  try {
    const video = await Video.findById(videoId);
    if (!video) {
      return res.status(404).send('Video not found');
    }
    console.log("Found video:", video);
      
    mongoose.connect('mongodb://127.0.0.1:27017/elearnhub');
    const conn = mongoose.connection;

    const fileId = new mongoose.Types.ObjectId(video.videoFileId); 
    
    const fileDeleteResult = await conn.collection('videos.files').deleteOne({ _id: fileId });
    console.log("File deletion result from fs.files:", fileDeleteResult);

    const chunkDeleteResult = await conn.collection('videos.chunks').deleteMany({ files_id: fileId });
    await user.updateOne(
      { },
      { $pull: { uploadedVideos: videoId } }
    );
    await Video.findByIdAndDelete(videoId);

    res.status(200).send('Video deleted successfully');
  } catch (error) {
    console.error('Error deleting video:', error);
    res.status(500).send('Server error');
  }
});


router.post('/watch-later/:videoId', async (req, res) => {
  console.log("go in this route");
  if (req.session.user) {
    const videoId = req.params.videoId;
    const userId = req.session.user._id; 
    console.log(videoId);
    try {
      const user = await User.findById(userId);
      if (!user) {
        return res.status(404).send('User not found');
      }
      if (user.watchLater.includes(videoId)) {
        return res.status(400).send('Video already in Watch Later list');
      }
      user.watchLater.push(videoId);
      await user.save();
      res.status(200).send('Video added to Watch Later');
    } catch (error) {
      console.error('Error adding video to Watch Later:', error);
      res.status(500).send('Server error');
    }
  } else {
    res.status(401).send('Unauthorized');
  }
});
router.get('/watch', async (req, res) => {
  if (!req.session.user) {
    return res.redirect('/login');
  }
  try {
    const user = await User.findById(req.session.user._id).populate('watchLater'); // Populate videos
    if (user.watchLater.length > 0) {
      const videos = await Video.find({
        _id: { $in: user.watchLater } // Fetch videos using the watchLater array
      }).populate('uploadedBy');
      res.render('watchlatter', { user: req.session.user, videos });
    } else {
      res.render('watchlatter', { user: req.session.user, videos: [] }); // No videos
    }
  } catch (err) {
    console.error('Error fetching watch later videos:', err);
    res.status(500).send('Server Error');
  }
});
router.get('/watch/:videoId', async (req, res) => {
  console.log("Entered the watchvideo route");
  if (req.session.user) {
    console.log("Entered the watchvideo running");
    const videoId = req.params.videoId;
    try {
      const video = await Video.findById(videoId).populate('uploadedBy', 'profileImage name');

      if (!video) {
        return res.status(404).send('Video not found');
      }
      res.render('watch', { user: req.session.user, video });

    } catch (error) {
      console.error('Error fetching video:', error);
      res.status(500).send('Server error');
    }
  } else {
    res.status(401).send('Unauthorized');
  }
});

router.get('/video/steam/:fileId', (req, res) => {
  const fileId = req.params.fileId;

  // Find the video file in GridFS by its ID
  gfs.files.findOne({ _id: mongoose.Types.ObjectId(fileId) }, (err, file) => {
      if (!file || file.length === 0) {
          return res.status(404).send('No file exists');
      }

      // Check if the file is an mp4 video
      if (file.contentType === 'video/mp4') {
          const readstream = gfs.createReadStream({ _id: file._id });

          // Set the content type and stream the file
          res.set('Content-Type', 'video/mp4');
          readstream.pipe(res);
      } else {
          res.status(404).send('Not a video file');
      }
  });
});
/*
 */
// @route GET /files
// @desc  Display all files in JSON
/*
router.get('/files', (req, res) => {
  gfs.files.find().toArray((err, files) => {
    if (!files || files.length === 0) {
      return res.status(404).json({
        err: 'No files exist',
      });
    }
    return res.json(files);
  });
});

// @route GET /files/:filename
// @desc  Display single file object
router.get('/files/:filename', (req, res) => {
  gfs.files.findOne({ filename: req.params.filename }, (err, file) => {
    if (!file || file.length === 0) {
      return res.status(404).json({
        err: 'No file exists',
      });
    }
    return res.json(file);
  });
});


// @route DELETE /files/:id
// @desc  Delete file
router.delete('/files/:id', (req, res) => {
  gfs.remove({ _id: req.params.id, root: 'uploads' }, (err) => {
    if (err) {
      return res.status(404).json({ err: err });
    }
    res.redirect('/');
  });
});*/

module.exports = router;
