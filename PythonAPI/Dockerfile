# Use an official Python runtime as a base image
FROM python:3.9-slim

# Set the working directory in the container
WORKDIR /app

# Install the required Python packages directly
RUN pip install --no-cache-dir --default-timeout=100 \
    Flask==2.0.3 \
    pandas==1.4.0 \
    numpy==1.21.0 \
    scikit-learn==1.0.2 \
    nltk==3.6.7

# Copy the current directory contents into the container at /app,
COPY . .

# Expose port 5000 to the host
EXPOSE 5000

# Run the Flask API when the container launches
CMD ["python", "app.py"]
