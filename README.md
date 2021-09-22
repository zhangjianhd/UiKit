## SlidingBlockTabView

仿MIUI相机切换TabLayout

#### 与ViewPage或ViewPage2一起使用：

- api:
```
bindContent(content: View, titles: MutableList<String>)
```
content传入ViewPager或ViewPager2，**需要先绑定adapter，会检验ViewPager的page个数和titles的个数**，内部处理了ViewPager的联动，只需要绑定即可

- 范例：[SlidingBlockViewPagerActivity](/app/src/main/java/com/zhangjian/samp/slidingblock/SlidingBlockViewPagerActivity.kt)
```
  val viewPager: ViewPager = findViewById(R.id.viewPager)
  val tabView: SlidingBlockTabView = findViewById(R.id.slidingBlock)
  val titles = mutableListOf<String>()
        titles.apply {
            add("page1")
            add("page2")
            add("page3")
            add("page4")
        }
  tabView.bindContent(viewPager, titles)
```
- 效果

![与ViewPage或ViewPage2一起使用](/image/与ViewPage或ViewPage2一起使用.gif)

#### 自由控制的滑块位置：

- api:
```
//绑定滑块标题以及点击每个滑块的事件
bindTabTitles(titles: MutableList<String>, click: (Int) -> Unit)
//控制主动滚动到某个位置
activeScrollToIndex(index: Int)
```
- 范例：[SlidingBlockActionActivity](/app/src/main/java/com/zhangjian/samp/slidingblock/SlidingBlockActionActivity.kt)
- 效果

![自由控制的滑块位置](/image/自由控制的滑块位置.gif)