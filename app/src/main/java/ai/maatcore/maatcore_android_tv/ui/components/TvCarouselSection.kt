package ai.maatcore.maatcore_android_tv.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.focusable // Added import for focusable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.ExperimentalTvMaterial3Api
import ai.maatcore.maatcore_android_tv.data.ContentItem
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorOrSable

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvCarouselSection(
    title: String,
    items: List<ContentItem>,
    onItemClick: (ContentItem) -> Unit, // Added onItemClick parameter
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(vertical = 16.dp)) {
        Text(
            text = title,
            color = MaatColorOrSable,
            fontSize = 22.sp,
            modifier = Modifier.padding(start = 60.dp, bottom = 8.dp)
        )
        TvLazyRow(
            contentPadding = PaddingValues(start = 60.dp, end = 60.dp),
            modifier = Modifier
                .height(200.dp)
                .focusable(enabled = false)
        ) {
            items(items) { item ->
                ContentCard(item = item, onClick = { onItemClick(item) }) // Pass item to onClick
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}
