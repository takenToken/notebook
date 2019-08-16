## vue指令集
[vue指令集文档](https://cn.vuejs.org/v2/api/#v-text)
* v-text 文本，等同于 {{msg}}
* v-model 双向绑定
* v-on:click 事件
* v-bind:title 动态地绑定一个或多个特性，或一个组件 prop 到表达式
* v-html 输出html内容

```html
在绑定 class 或 style 特性时，支持其它类型的值，如数组或对象。

<!-- 绑定一个属性 -->
<img v-bind:src="imageSrc">

<!-- 动态特性名 (2.6.0+) -->
<button v-bind:[key]="value"></button>

<!-- 缩写 -->
<img :src="imageSrc">

<!-- 动态特性名缩写 (2.6.0+) -->
<button :[key]="value"></button>

<!-- 内联字符串拼接 -->
<img :src="'/path/to/images/' + fileName">

<!-- class 绑定 -->
<div :class="{ red: isRed }"></div>
<div :class="[classA, classB]"></div>
<div :class="[classA, { classB: isB, classC: isC }]">

<!-- style 绑定 -->
<div :style="{ fontSize: size + 'px' }"></div>
<div :style="[styleObjectA, styleObjectB]"></div>

<!-- 绑定一个有属性的对象 -->
<div v-bind="{ id: someProp, 'other-attr': otherProp }"></div>

<!-- 通过 prop 修饰符绑定 DOM 属性 -->
<div v-bind:text-content.prop="text"></div>

<!-- prop 绑定。“prop”必须在 my-component 中声明。-->
<my-component :prop="someThing"></my-component>

<!-- 通过 $props 将父组件的 props 一起传给子组件 -->
<child-component v-bind="$props"></child-component>

<!-- XLink -->
<svg><a :xlink:special="foo"></a></svg>
```

* v-if/v-else/v-else-if  if判断
* v-for 循环
* v-slot 提供具名插槽或需要接收 prop 的插槽。
* v-pre 跳过这个元素和它的子元素的编译过程。可以用来显示原始 Mustache 标签。跳过大量没有指令的节点会加快编译。
* v-cloak 这个指令保持在元素上直到关联实例结束编译。和 CSS 规则如 [v-cloak] { display: none } 一起用时，这个指令可以隐藏未编译的 Mustache 标签直到实例准备完毕
* v-once 只渲染元素和组件一次。随后的重新渲染，元素/组件及其所有的子节点将被视为静态内容并跳过。这可以用于优化更新性能
* 自定义组件

```javascript
//自定义组件
Vue.component("todo-item",{
    template:'<li>这是一个待办事项</li>'
});
```

## 主要知识点
* vue指令
* vue组件
* vue单文件template模板
* props 数据传递
* slot 内容分发
* events,$emit,@click事件
