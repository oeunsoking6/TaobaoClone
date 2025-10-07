# Use an official lightweight Node.js runtime as a parent image
FROM node:18-slim

# Set the working directory in the container to /app
WORKDIR /app

# Copy package.json and package-lock.json from the user-service subfolder
COPY backend/user-service/package*.json ./

# Install any needed packages
RUN npm install

# Copy the rest of the application code from the user-service subfolder
COPY backend/user-service/ .

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Define the command to run your app
CMD [ "node", "index.js" ]