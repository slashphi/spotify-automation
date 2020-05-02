# spotify-automation
That's a simple service which (currently configured as aws lambda function) which automates the synchronization of spotify podcasts with playlists

# Configuration
- Configure your application.yml to include:
  - Spotify access token
  - Spotify refresh token
  - Spotify client id
  - Spotify client credentials
  
# Build
`mvn clean package`

# Just run on AWS Lambda
