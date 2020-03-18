package kz.kase.test


//
//class TestOldTableActivity: AppCompatActivity(), MyRecyclerViewAdapter.ItemClickListener {
//
//    var data = ArrayList<String>()
//
//    var adapter: MyRecyclerViewAdapter? = null
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        // data to populate the RecyclerView with
//
//        data.add("Horse")
//        data.add("Cow")
//        data.add("Camel")
//        data.add("Sheep")
//        data.add("Goat")
//
//        // set up the RecyclerView
//        val recyclerView = findViewById(R.id.rvAnimals)
//        val layoutManager = LinearLayoutManager(this)
//        recyclerView.setLayoutManager(layoutManager)
//        val dividerItemDecoration = DividerItemDecoration(recyclerView.getContext(),
//                layoutManager.orientation)
//        recyclerView.addItemDecoration(dividerItemDecoration)
//        adapter = MyRecyclerViewAdapter(this, data)
//        adapter.setClickListener(this)
//        recyclerView.setAdapter(adapter)
//    }
//    fun onItemClick(view: View, position: Int) {
//        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show()
//    }
//
//    fun onButtonClick(view: View) {
//        insertSingleItem()
//    }
//
//    private fun insertSingleItem() {
//        val item = "Pig"
//        val insertIndex = 2
//        data.add(insertIndex, item)
//        adapter.notifyItemInserted(insertIndex)
//    }
//
//    private fun insertMultipleItems() {
//        val items = ArrayList()
//        items.add("Pig")
//        items.add("Chicken")
//        items.add("Dog")
//        val insertIndex = 2
//        data.addAll(insertIndex, items)
//        adapter.notifyItemRangeInserted(insertIndex, items.size())
//    }
//
//    private fun removeSingleItem() {
//        val removeIndex = 2
//        data.remove(removeIndex)
//        adapter.notifyItemRemoved(removeIndex)
//    }
//
//    private fun removeMultipleItems() {
//        val startIndex = 2 // inclusive
//        val endIndex = 4   // exclusive
//        val count = endIndex - startIndex // 2 items will be removed
//        data.subList(startIndex, endIndex).clear()
//        adapter.notifyItemRangeRemoved(startIndex, count)
//    }
//
//    private fun removeAllItems() {
//        data.clear()
//        adapter.notifyDataSetChanged()
//    }
//
//    private fun replaceOldListWithNewList() {
//        // clear old list
//        data.clear()
//
//        // add new list
//        val newList = ArrayList()
//        newList.add("Lion")
//        newList.add("Wolf")
//        newList.add("Bear")
//        data.addAll(newList)
//
//        // notify adapter
//        adapter.notifyDataSetChanged()
//    }
//
//    private fun updateSingleItem() {
//        val newValue = "I like sheep."
//        val updateIndex = 3
//        data[updateIndex] = newValue
//        adapter.notifyItemChanged(updateIndex)
//    }
//
//    private fun moveSingleItem() {
//        val fromPosition = 3
//        val toPosition = 1
//
//        // update data array
//        val item = data.get(fromPosition)
//        data.remove(fromPosition)
//        data.add(toPosition, item)
//
//        // notify adapter
//        adapter.notifyItemMoved(fromPosition, toPosition)
//    }
//}