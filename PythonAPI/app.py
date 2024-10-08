from flask import Flask, request, jsonify
import pickle
import pandas as pd

app = Flask(__name__)

# Load the trained model and data
movie_dict = pickle.load(open('movie_dict.pkl', 'rb'))
movies = pd.DataFrame(movie_dict)
similarity = pickle.load(open('similarity.pkl', 'rb'))


def recommend(movie):
    movie_index = movies[movies['title'] == movie].index[0]
    distances = similarity[movie_index]
    movies_list = sorted(list(enumerate(distances)), reverse=True, key=lambda x: x[1])[1:6]
    recommended_movies = [movies.iloc[i[0]].title for i in movies_list]
    return recommended_movies


@app.route('/recommend', methods=['GET'])
def recommend_movies():
    movie = request.args.get('movie')
    print(movie)
    if not movie:
        return jsonify({"error": "Movie name is required"}), 400

    recommendations = recommend(movie)
    return jsonify({"recommendations": recommendations})


if __name__ == '__main__':
    app.run(host='0.0.0.0')

