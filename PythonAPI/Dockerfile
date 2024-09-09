FROM python

# Create a directory for your application
WORKDIR /app

# Copy your requirements.txt file
COPY requirements.txt .

# Install dependencies from requirements.txt
RUN pip install -r requirements.txt

# Copy your .pkl files to the container
COPY . .

# Expose the port your Flask app will listen on
EXPOSE 5000

# Specify the command to run when the container starts
CMD ["python", "app.py"]