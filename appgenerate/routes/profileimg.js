const fs = require('fs');
const Student = require('../models/User');
var express = require('express');
var router = express.Router();
const mongoose = require('mongoose');

// Function to update students with profile images
router.get('/profileimgsave', async (req, res) => {
    try {
        const students = await Student.find(); // Fetch all student documents

        for (let student of students) {
            // Image path based on studentId (ensure your images are named like studentId.jpg)
            const imagePath = `./public/profileimg/${student.userId}.jpeg`;
            console.log(imagePath);

            try {
                // Read the image and convert it to Base64
                if (fs.existsSync(imagePath)) {
                    const imageBuffer = fs.readFileSync(imagePath);  // Read image file
                    const imageBase64 = imageBuffer.toString('base64');  // Convert to Base64

                    // Add Base64 image to the student's document
                    await Student.updateOne(
                        { userId: student.userId },  // Find student by ID
                        { $set: { profileImage: `data:profileimg/jpeg;base64,${imageBase64}` } }  // Add profileImage field
                    );
                    console.log(`Updated student ${student.userId} with profile image`);
                } else {
                    console.log(`Image for student ${student.userId} not found`);
                }
            } catch (err) {
                console.error(`Error updating student ${student.userId}:`, err);
            }
        }

        res.send('Profile images updated successfully');
    } catch (err) {
        console.error('Error fetching students:', err);
        res.status(500).send('Error updating profile images');
    }
});

module.exports = router;
