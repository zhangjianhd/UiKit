[demo下载地址](/apk/app-debug.apk)

## 功能列表

| view                                                                                             | 描述                   |
|--------------------------------------------------------------------------------------------------|----------------------|
| [SlidingBlockTabView](/uikit/src/main/java/com/zhangjian/uikit/tab/SlidingBlockTabView.kt)       | 仿MIUI12相册切换TabLayout |
| [SlidingBlockScopeView](/uikit/src/main/java/com/zhangjian/uikit/scope/SlidingBlockScopeView.kt) | 通过滑块获取区间范围，常用于价格范围   |
| [PieChart](/uikit/src/main/java/com/zhangjian/uikit/chart/PieChart.kt)                           | 饼状图，可带衔接切角，美化样式      |

### SlidingBlockTabView

仿MIUI12相册切换TabLayout

#### 与ViewPage或ViewPage2一起使用：

- api:

```
bindContent(content: View, titles: MutableList<String>)
```

content传入ViewPager或ViewPager2，**需要先绑定adapter，会检验ViewPager的page个数和titles的个数**
，内部处理了ViewPager的联动，只需要绑定即可

-
范例：[SlidingBlockViewPagerActivity](/app/src/main/java/com/zhangjian/samp/slidingblock/SlidingBlockViewPagerActivity.kt)

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

-
范例：[SlidingBlockActionActivity](/app/src/main/java/com/zhangjian/samp/slidingblock/SlidingBlockActionActivity.kt)
- 效果

![自由控制的滑块位置](/image/自由控制的滑块位置.gif)

### SlidingBlockScopeView

可拖拽滑块选取价格区间，效果：

![滑块调整区间](/image/滑块选取价格区间.png)

- **采用适配器方式，自由定制滑块数据的提供**

  ```kotlin
  interface SlidingBlockScopeAdapter {
  
      /**
       * 一共有多少个点
       */
      fun getSize(): Int
  
      /**
       * 某一点被选中时的显示文案
       */
      fun attachShow(position: Int): String
  
      /**
       * 滑块改变时的回调通知
       */
      fun onScopeChange(leftPoint: Int, rightPoint: Int)
  }
  ```

  通过适配器提供一共多少个点getSize以及每个点渲染的文案attachShow，范围变化时通过onScopeChange回调给调用者，回调的是点的位置，调用者根据自己需求自由转换。

  其中默认实现了一个价格范围的适配器实现，可作为参考：PriceSlidingBlockScopeAdapter

- api

  ```kotlin
  //设置适配器，并且可以默认设置左右的选中点，内部会校验越界以及做有点大小比较，不符合时抛出异常
  fun setAdapter(
          adapter: SlidingBlockScopeAdapter,
          leftIndex: Int? = null,
          rightIndex: Int? = null
      )
  //主动设置左右当前选中点 
  fun setIndex(leftIndex: Int, rightIndex: Int)
  ```

-
范例：[SlidingBlockScopeActivity](/app/src/main/java/com/zhangjian/samp/scop/SlidingBlockScopeActivity.kt)

### PieChart

饼状图，可实现连接处切角。效果：

![饼状图](/image/饼图.png)

- 支持属性配置：

  | 属性值 | 说明 |
  ----------------| -------------- | ------------------ |
  | ringWidth | 圆环宽度 |
  | coverRingWidth | 内层蒙层宽度 |
  | cornerCut | 是否衔接处绘制切角 |
  | divisionRadius | 切角的圆角大小 |

- 接收数据源DataBean：

  ```
  values: Float       //数值，无需是计算后所占的百分比，内部会根据传入的集合自动分配
  @ColorInt val color: Int    //颜色值
  @ColorInt val colorInner: Int? = null  //内圈蒙层颜色值，可不传，有需要可以传
  ```

  设置数据

  ```kotlin
  /**
   * @param dataBeans 数据集合
   * @param sort 是否启用从大到小排序
   */
  fun setData(dataBeans: List<DataBean>?, sort: Boolean = false) 
  ```

- 范例：[PieChartActivity](/app/src/main/java/com/zhangjian/samp/chart/PieChartActivity.kt)