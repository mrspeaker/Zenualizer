package services

object HackerNews {
  def news = Twitter.userTimeline("hnycombinator")
}
