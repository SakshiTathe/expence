// routes/stream.js
const express = require('express');
const router = express.Router();
const mongoose = require('mongoose');
const Grid = require('gridfs-stream');

// Initialize GridFS stream
const conn = mongoose.createConnection('mongodb://localhost:27017/yourDB');
let gfs;
conn.once('open', () => {
  gfs = Grid(conn.db, mongoose.mongo);
  gfs.collection('uploads');
});

// Streaming video from GridFS
router.get('/video/stream/:videoId', (req, res) => {
  gfs.files.findOne({ _id: mongoose.Types.ObjectId(req.params.videoId) }, (err, file) => {
    if (!file || file.length === 0) {
      return res.status(404).json({ err: 'No file exists' });
    }
    const readstream = gfs.createReadStream(file.filename);
    readstream.pipe(res);
  });
});

module.exports = router;
