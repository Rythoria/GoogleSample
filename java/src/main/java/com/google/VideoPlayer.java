package com.google;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map;


public class VideoPlayer {

  private final VideoLibrary videoLibrary;
  private Video currentVideo;
  private String currVideoTitle; 
  boolean isPaused = false;
  ArrayList<ArrayList<Object>> playlists = new ArrayList<ArrayList<Object>>(); 


  public VideoPlayer() {
    this.videoLibrary = new VideoLibrary();
  }

  public void numberOfVideos() {
    System.out.printf("%s videos in the library%n", videoLibrary.getVideos().size());
  }

  public void showAllVideos() {
    System.out.println("Here's a list of all available videos:");
    
    for (Video video : videoLibrary.getVideos()) {
      String[] splitTags = video.getTags().toString().split(",");
      System.out.printf("%s (%s) ",video.getTitle(), video.getVideoId());
      for (String tag : splitTags) {
        System.out.print(tag);
      }
      System.out.println();
    }
  }

  public void playVideo(String videoId) {
    try {
      if(currentVideo != null) {
        stopVideo();
      }
      Video video = videoLibrary.getVideo(videoId);
      System.out.println("Playing video: "+video.getTitle());
      currentVideo = video;
      currVideoTitle = currentVideo.getTitle();
      isPaused = false;
    } 
    catch (Exception e) {
      System.out.println("Cannot play video: Video does not exist");
    }
  }

  public void stopVideo() {
    try {
      if(currentVideo == null) {
        System.out.println("Cannot stop video: No video is currently playing");
      }
      else {
        System.out.println("Stopping video: "+currVideoTitle);
        currentVideo = null;
      }
    } catch (Exception e) {
      System.out.println("Something went wrong!");
    }
  }

  public void playRandomVideo() {
    Video random = videoLibrary.getVideos().get(new Random().nextInt(videoLibrary.getVideos().size()));
    playVideo(random.getVideoId());

  }

  public void pauseVideo() {
    if(isPaused == false && currentVideo != null) {
      System.out.println("Pausing video: "+currVideoTitle);
      isPaused = true;
    }
    else if (isPaused == true && currentVideo != null) {
      System.out.println("Video already paused: "+currVideoTitle);
    }
    else if(currentVideo == null) {
      System.out.println("Cannot pause video: No video is currently playing");
    }
  }

  public void continueVideo() {
    try {
      if(isPaused == true && currentVideo != null) {
        System.out.println("Continuing video: "+currVideoTitle);
        isPaused = false;
      }
      else if (isPaused == false && currentVideo != null) {
        System.out.println("Cannot continue video: Video is not paused");
      }
      else if(currentVideo == null) {
        System.out.println("Cannot continue video: No video is currently playing");
      }
    } catch (Exception e) {
    System.out.println("An error occurred continuing video: "+e);
    }
  }

  public void showPlaying() {
    try {
      if(currentVideo != null) {
        String[] splitTags = currentVideo.getTags().toString().split(",");
        if(isPaused == false) {
          System.out.printf("Currently playing: %s (%s) ",currentVideo.getTitle(), currentVideo.getVideoId());
          for (String tag : splitTags) {
            System.out.print(tag);
          }
          System.out.println();
        }
        else {
          System.out.printf("Currently playing: %s (%s) ",currentVideo.getTitle(), currentVideo.getVideoId());
          for (String tag : splitTags) {
            System.out.print(tag);
          }
          System.out.println(" - PAUSED");
        }
      }
      else if(currentVideo == null){
        System.out.println("No video is currently playing");
      }
    }
    catch(Exception e) {
      System.out.println("An error occurred: "+e);
    }
  }

  Map<String, VideoPlaylist> playListMap = new HashMap<>();

  public void createPlaylist(String playlistName) {
    String lowerCaseName = playlistName.toLowerCase();
    if (playListMap.containsKey(lowerCaseName)) {
      System.out.println("Cannot create playlist: A playlist with the same name already exists");
    } else {
      playListMap.put(lowerCaseName, new VideoPlaylist(playlistName));
      System.out.println("Successfully created new playlist: " + playlistName);
    }
  }

  public void addVideoToPlaylist(String playlistName, String videoId) {
    String lowerCaseName = playlistName.toLowerCase();
    if (playListMap.containsKey(lowerCaseName)) {
      Video video = videoLibrary.getVideo(videoId);
      VideoPlaylist playList = playListMap.get(lowerCaseName);
      if (video != null) {
        if (flags.containsKey(videoId)) {
          System.out.printf("Cannot add video to %s: "
                  + "Video is currently flagged (reason: %s)%n", playlistName, flags.get(videoId));
          return;
        }
        if (playList.addVideo(video)) {
          System.out.printf("Added video to %s: %s%n", playlistName, video.getTitle());
        } else {
          System.out.printf("Cannot add video to %s: Video already added%n", playlistName);
        }
      } else {
        System.out.printf("Cannot add video to %s: Video does not exist%n", playlistName);
      }
    } else {
      System.out.printf("Cannot add video to %s: Playlist does not exist%n", playlistName);
    }
  }

  public void showAllPlaylists() {
    List<String> lowerCaseNames = new ArrayList<>(playListMap.keySet());
    lowerCaseNames.sort(CharSequence::compare);
    if (lowerCaseNames.isEmpty()) {
      System.out.println("No playlists exist yet");
    } else {
      System.out.println("Showing all playlists:");
      lowerCaseNames.forEach(
              n -> System.out.println(playListMap.get(n).name)
      );
    }
  }

  public void showPlaylist(String playlistName) {
    VideoPlaylist playList = playListMap.get(playlistName.toLowerCase());
    if (playList != null) {
      System.out.printf("Showing playlist: %s%n", playlistName);
      if (playList.videos.isEmpty()) {
        System.out.println("  No videos here yet");
      } else {
        playList.videos.forEach(
                v -> System.out.printf("%s (%s) %s",v.getTitle(), v.getVideoId(), v.getTags())
        );
      }
    } else {
      System.out.printf("Cannot show playlist %s: Playlist does not exist%n", playlistName);
    }
  }

  public void removeFromPlaylist(String playlistName, String videoId) {
    String lowerCaseName = playlistName.toLowerCase();
    VideoPlaylist playList = playListMap.get(lowerCaseName);
    if (playList != null) {
      Video video = videoLibrary.getVideo(videoId);
      if (video != null) {
        if (playList.removeVideo(video)) {
          System.out.printf("Removed video from %s: %s%n", playlistName, video.getTitle());
        } else {
          System.out.printf("Cannot remove video from %s: Video is not in playlist%n",
                  playlistName);
        }
      } else {
        System.out.printf("Cannot remove video from %s: Video does not exist%n", playlistName);
      }
    } else {
      System.out.printf("Cannot remove video from %s: Playlist does not exist%n", playlistName);
    }
  }

  public void clearPlaylist(String playlistName) {
    String lowerCaseName = playlistName.toLowerCase();
    VideoPlaylist playList = playListMap.get(lowerCaseName);
    if (playList != null) {
      playList.videos = new ArrayList<>();
      System.out.println("Successfully removed all videos from " + playlistName);
    } else {
      System.out.printf("Cannot clear playlist %s: Playlist does not exist%n", playlistName);
    }
  }

  public void deletePlaylist(String playlistName) {
    String lowerCaseName = playlistName.toLowerCase();
    if (playListMap.containsKey(lowerCaseName)) {
      playListMap.remove(lowerCaseName);
      System.out.println("Deleted playlist: " + playlistName);
    } else {
      System.out.printf("Cannot delete playlist %s: Playlist does not exist%n", playlistName);
    }
  }

  public void searchVideos(String searchTerm) {
    System.out.println("Here are the results for "+searchTerm+":");

    for(Video video: videoLibrary.getVideos()) {
      String videoTitle = video.getTitle().toLowerCase();
      if (videoTitle.contains(searchTerm.toLowerCase())) {
        String[] splitTags = video.getTags().toString().split(",");
        System.out.printf("%s (%s) ",video.getTitle(), video.getVideoId());
        for (String tag : splitTags) {
          System.out.print(tag);
        }
        System.out.println();
      }
    }
  }

  public void searchVideosWithTag(String videoTag) {
    System.out.println("searchVideosWithTag needs implementation");
  }

  Map<String, String> flags = new HashMap<>();

  public void flagVideo(String videoId) {
    flagVideo(videoId, "Not supplied");
  }

  public void flagVideo(String videoId, String reason) {
    Video video = videoLibrary.getVideo(videoId);
    if (video != null) {
      if (currentVideo != null && currentVideo.getVideoId().equals(videoId)) {
        stopVideo();
      }
      if (flags.containsKey(videoId)) {
        System.out.println("Cannot flag video: Video is already flagged");
      } else {
        flags.put(videoId, reason);
        System.out.printf("Successfully flagged video: %s (reason: %s)%n",
                video.getTitle(), reason);
      }
    } else {
      System.out.println("Cannot flag video: Video does not exist");
    }
  }

  public void allowVideo(String videoId) {
    Video video = videoLibrary.getVideo(videoId);
    if (video != null) {
      if (!flags.containsKey(videoId)) {
        System.out.println("Cannot remove flag from video: Video is not flagged");
      } else {
        flags.remove(videoId);
        System.out.printf("Successfully removed flag from video: %s%n", video.getTitle());
      }
    } else {
      System.out.println("Cannot remove flag from video: Video does not exist");
    }
  }
  
}