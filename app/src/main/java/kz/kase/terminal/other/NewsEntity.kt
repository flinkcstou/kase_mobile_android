package kz.kase.terminal.other

class NewsEntity {
    data class NewsItem(
        val title: String,
        val link: String,
        val pubDate: String
    )
}