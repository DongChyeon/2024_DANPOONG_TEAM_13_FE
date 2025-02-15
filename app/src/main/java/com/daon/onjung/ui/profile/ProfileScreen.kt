package com.daon.onjung.ui.profile

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.daon.onjung.OnjungAppState
import com.daon.onjung.R
import com.daon.onjung.Routes
import com.daon.onjung.ui.theme.OnjungTheme
import com.daon.onjung.util.formatCurrency
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun ProfileScreen(
    appState: OnjungAppState,
    viewModel: ProfileViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val effectFlow = viewModel.effect

    LaunchedEffect(Unit) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                is ProfileContract.Effect.NavigateTo -> {
                    appState.navController.navigate(effect.destination, effect.navOptions)
                }

                is ProfileContract.Effect.ShowSnackBar -> {
                    appState.showSnackBar(effect.message)
                }
            }
        }
    }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val offsetDistance = screenWidth / 3

    var offset by remember { mutableStateOf(0.dp) }
    val animatedOffset by animateDpAsState(
        targetValue = offset,
        animationSpec = tween(
            durationMillis = 1000,
            easing = LinearEasing
        )
    )

    LaunchedEffect(Unit) {
        while (true) {
            offset = (-2).dp
            delay(1000L)
            offset = 4.dp
            delay(1000L)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_profile_character),
            contentDescription = "IC_PROFILE_CHARACTER",
            modifier = Modifier.align(Alignment.Center),
        )

        if (uiState.storeList.isEmpty()) {
            Image(
                painter = painterResource(id = R.drawable.ic_ballon_no_warmth),
                contentDescription = "IC_BALLON_NO_WARMTH",
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-54).dp)
            )
        }

        Column(modifier = Modifier.align(Alignment.TopCenter)) {
            ProfileAppBar(
                ticketCount = uiState.ticketCount
            ) {
                appState.navigate(Routes.Profile.TICKET_LIST)
            }

            ProfileHeader(
                restaurantCount = uiState.onjungCount,
                onShowAll = {
                    appState.navigate(Routes.Profile.RESTAURANT_LIST)
                }
            )
        }

        ProfileSummary(
            modifier = Modifier.align(Alignment.BottomCenter),
            warmthDonationCount = uiState.onjungBrief.totalOnjungCount,
            totalWarmthDonationAmount = uiState.onjungBrief.totalOnjungAmount
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            uiState.storeList.forEachIndexed { index, restaurant ->
                when (index) {
                    0 -> {
                        ProfileRestaurantItem(
                            name = restaurant,
                            modifier = Modifier.offset(x = 0.dp, y = -offsetDistance + animatedOffset) // 위쪽
                        )
                    }
                    1 -> {
                        ProfileRestaurantItem(
                            name = restaurant,
                            modifier = Modifier.offset(x = offsetDistance, y = -offsetDistance / 3 + animatedOffset) // 오른쪽
                        )
                    }
                    2 -> {
                        ProfileRestaurantItem(
                            name = restaurant,
                            modifier = Modifier.offset(x = -offsetDistance, y = -offsetDistance / 3 + animatedOffset) // 왼쪽
                        )
                    }
                    3 -> {
                        ProfileRestaurantItem(
                            name = restaurant,
                            modifier = Modifier.offset(x = -offsetDistance / 1.5f, y = offsetDistance / 1.5f + animatedOffset) // 왼쪽 아래 대각선
                        )
                    }
                    4 -> {
                        ProfileRestaurantItem(
                            name = restaurant,
                            modifier = Modifier.offset(x = offsetDistance / 1.5f, y = offsetDistance / 1.5f + animatedOffset) // 오른쪽 아래 대각선
                        )
                    }
                }
            }

            if (uiState.remainCount > 0) {
                Text(
                    "+${uiState.remainCount}",
                    modifier = Modifier.offset(
                        y = 50.dp,
                    ),
                    style = OnjungTheme.typography.body2.copy(
                        color = OnjungTheme.colors.main_coral
                    )
                )
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    restaurantCount: Int,
    onShowAll: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 24.dp,
                start = 20.dp,
                end = 20.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "함께한 식당",
                style = OnjungTheme.typography.h2.copy(
                    color = OnjungTheme.colors.text_1
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "총 ${restaurantCount}개",
                style = OnjungTheme.typography.caption.copy(
                    color = OnjungTheme.colors.text_3
                )
            )
        }

        Row(
            modifier = Modifier.clickable { onShowAll() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "전체보기",
                style = OnjungTheme.typography.body2.copy(
                    color = OnjungTheme.colors.text_1
                )
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = null,
                tint = OnjungTheme.colors.text_1
            )
        }
    }
}

@Composable
private fun ProfileAppBar(
    modifier: Modifier = Modifier,
    ticketCount: Int,
    onTicketClick: () -> Unit
) {
    Column {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .shadow(elevation = 5.dp)
                .background(OnjungTheme.colors.white)
                .height(60.dp)
                .padding(horizontal = 20.dp)
                .statusBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "나의 온기",
                style = OnjungTheme.typography.h2.copy(
                    fontWeight = FontWeight.Bold,
                )
            )

            Box(
                modifier = Modifier
                    .background(
                        color = OnjungTheme.colors.gray_3,
                        shape = CircleShape
                    )
                    .border(
                        width = 1.dp,
                        color = OnjungTheme.colors.gray_1,
                        shape = CircleShape
                    )
                    .clip(shape = CircleShape)
                    .align(Alignment.CenterEnd)
                    .clickable { onTicketClick() }
            ) {
                Row(
                    modifier = Modifier
                        .padding(
                            horizontal = 10.dp,
                            vertical = 2.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_meal_ticket_chip),
                        contentDescription = null,
                        tint = if (ticketCount > 0) OnjungTheme.colors.main_coral else OnjungTheme.colors.gray_1,
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = ticketCount.toString(),
                        style = OnjungTheme.typography.body1.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = OnjungTheme.colors.text_1
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileRestaurantItem(
    modifier: Modifier = Modifier,
    name: String
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_restaurant),
            contentDescription = "IC_RESTAURANT",
            tint = Color.Unspecified
        )

        Text(
            text = name,
            modifier = Modifier.width(90.dp),
            maxLines = 1,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            style = OnjungTheme.typography.caption.copy(
                color = Color(0xFF616161)
            )
        )
    }
}

@Composable
private fun ProfileSummary(
    modifier: Modifier = Modifier,
    warmthDonationCount: Int,
    totalWarmthDonationAmount: Int
) {
    Column(
        modifier = modifier.padding(
            horizontal = 20.dp,
            vertical = 24.dp
        )
    ) {
        Text(
            text = "온정과 함께한 선순환",
            style = OnjungTheme.typography.h2.copy(
                color = OnjungTheme.colors.text_1
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            shape = RoundedCornerShape(10.dp),
            color = OnjungTheme.colors.white,
            shadowElevation = 10.dp
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = 22.dp,
                    vertical = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "온기 전달 횟수",
                        style = OnjungTheme.typography.body2.copy(
                            color = OnjungTheme.colors.text_1
                        )
                    )

                    Text(
                        text = "$warmthDonationCount 건",
                        style = OnjungTheme.typography.body2.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = OnjungTheme.colors.text_1
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "전한 온기 금액",
                        style = OnjungTheme.typography.body2.copy(
                            color = OnjungTheme.colors.text_1
                        )
                    )

                    Text(
                        text = formatCurrency(totalWarmthDonationAmount),
                        style = OnjungTheme.typography.body2.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = OnjungTheme.colors.text_1
                        )
                    )
                }
            }
        }
    }
}