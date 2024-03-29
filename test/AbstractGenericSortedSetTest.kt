import org.junit.jupiter.api.Assertions.*
import java.util.*

abstract class AbstractGenericSortedSetTest {
    private lateinit var tree: SortedSet<Int>
    private lateinit var randomTree: SortedSet<Int>
    private val randomTreeSize = 1000
    private val randomValues = mutableListOf<Int>()
    private val treeSet = TreeSet<Int>()

    protected fun fillTree(create: () -> SortedSet<Int>) {
        this.tree = create()
        //В произвольном порядке добавим числа от 1 до 10
        tree.add(5)
        tree.add(1)
        tree.add(2)
        tree.add(7)
        tree.add(9)
        tree.add(10)
        tree.add(8)
        tree.add(4)
        tree.add(3)
        tree.add(6)

        this.randomTree = create()
        val random = Random()
        for (i in 0 until randomTreeSize) {
            val randomValue = random.nextInt(randomTreeSize) + 1
            if (randomTree.add(randomValue)) {
                randomValues.add(randomValue)
            }
        }

        for (element in tree)
            treeSet += element
    }

    protected fun doHeadSetTest() {
        var set: SortedSet<Int> = tree.headSet(5)
        assertTrue(set.contains(1))
        assertTrue(set.contains(2))
        assertTrue(set.contains(3))
        assertTrue(set.contains(4))
        assertFalse(set.contains(5))
        assertFalse(set.contains(6))
        assertFalse(set.contains(7))
        assertFalse(set.contains(8))
        assertFalse(set.contains(9))
        assertFalse(set.contains(10))

        set = tree.headSet(127)
        val setTreeSet = treeSet.headSet(127)
        for (i in 1..10)
            assertTrue(set.contains(i))
        assertEquals(set, setTreeSet)
    }

    protected fun doTailSetTest() {
        var set: SortedSet<Int> = tree.tailSet(5)
        assertFalse(set.contains(1))
        assertFalse(set.contains(2))
        assertFalse(set.contains(3))
        assertFalse(set.contains(4))
        assertTrue(set.contains(5))
        assertTrue(set.contains(6))
        assertTrue(set.contains(7))
        assertTrue(set.contains(8))
        assertTrue(set.contains(9))
        assertTrue(set.contains(10))

        set = tree.tailSet(-128)
        val setTreeSet = treeSet.tailSet(-128)
        for (i in 1..10)
            assertTrue(set.contains(i))
        assertEquals(set, setTreeSet)
    }

    protected fun doHeadSetRelationTest() {
        val set: SortedSet<Int> = tree.headSet(7)
        val setTreeSet = treeSet.headSet(7)
        assertEquals(setTreeSet.size, set.size)
        assertEquals(treeSet.size, tree.size)
        tree.add(0)
        treeSet.add(0)
        assertTrue(set.contains(0))
        assertEquals(setTreeSet.contains(0), set.contains(0))
        set.add(-2)
        setTreeSet.add(-2)
        assertEquals(treeSet.contains(-2), tree.contains(-2))
        tree.add(12)
        treeSet.add(12)
        assertEquals(setTreeSet.contains(12), set.contains(12))
        assertThrows(IllegalArgumentException::class.java) { set.add(8) }
        assertEquals(setTreeSet.size, set.size)
        assertEquals(treeSet.size, tree.size)
    }

    protected fun doTailSetRelationTest() {
        val set: SortedSet<Int> = tree.tailSet(4)
        assertEquals(7, set.size)
        assertEquals(10, tree.size)
        tree.add(12)
        assertTrue(set.contains(12))
        set.add(42)
        assertTrue(tree.contains(42))
        tree.add(0)
        assertFalse(set.contains(0))
        assertThrows(IllegalArgumentException::class.java) { set.add(-2) }

        assertEquals(9, set.size)
        assertEquals(13, tree.size)
    }

    protected fun doSubSetTest() {
        val smallSet: SortedSet<Int> = tree.subSet(3, 8)
        assertFalse(smallSet.contains(1))
        assertFalse(smallSet.contains(2))
        assertTrue(smallSet.contains(3))
        assertTrue(smallSet.contains(4))
        assertTrue(smallSet.contains(5))
        assertTrue(smallSet.contains(6))
        assertTrue(smallSet.contains(7))
        assertFalse(smallSet.contains(8))
        assertFalse(smallSet.contains(9))
        assertFalse(smallSet.contains(10))

        assertThrows(IllegalArgumentException::class.java) { smallSet.add(2) }
        assertThrows(IllegalArgumentException::class.java) { smallSet.add(9) }

        val smallTreeSet = treeSet.subSet(3, 8)
        for (element in smallTreeSet)
            assertTrue(smallSet.contains(element))
        assertEquals(smallTreeSet.size, smallSet.size)

        val allSet = tree.subSet(-128, 128)
        for (i in 1..10)
            assertEquals(true, allSet.contains(i))
        val allTreeSet = treeSet.subSet(-128, 128)
        assertEquals(allTreeSet, allSet)

        val random = Random()
        val toElement = random.nextInt(randomTreeSize) + 1
        val fromElement = random.nextInt(toElement - 1) + 1

        val randomSubset = randomTree.subSet(fromElement, toElement)
        randomValues.forEach { element ->
            assertEquals(element in fromElement until toElement, randomSubset.contains(element))
        }
    }

    protected fun doSubSetRelationTest() {
        val set: SortedSet<Int> = tree.subSet(2, 15)
        val setTreeSet = treeSet.subSet(2, 15)
        assertEquals(setTreeSet.size, set.size)
        assertEquals(treeSet.size, tree.size)
        assertEquals(setTreeSet, set)
        tree.add(11)
        assertTrue(set.contains(11))
        set.add(14)
        assertTrue(tree.contains(14))
        tree.add(0)
        assertFalse(set.contains(0))
        tree.add(15)
        assertFalse(set.contains(15))
        setTreeSet.addAll(listOf(11, 14))
        assertEquals(setTreeSet, set)
        assertThrows(IllegalArgumentException::class.java) { set.add(1) }
        assertThrows(IllegalArgumentException::class.java) { set.add(20) }

        assertEquals(setTreeSet.size, set.size)
        assertEquals(treeSet.size + 2, tree.size)
    }

    protected fun doTestGenericSortedSetIterator() {
        val scapeGoatTree = ScapeGoatTree<Int>()
        val ktSortedSetTail = sortedSetOf<Int>()
        val ktSortedSetHead = sortedSetOf<Int>()
        val ktSortedSetSubset = sortedSetOf<Int>()
        val rand = Random()
        val fromElement = rand.nextInt(24)
        val toElement = rand.nextInt(29)
        for (i in 0..255 step fromElement) {
            scapeGoatTree.add(i)
            if (i < toElement)
                ktSortedSetHead.add(i)
            if (i in fromElement until toElement)
                ktSortedSetSubset.add(i)
            if (i >= fromElement)
                ktSortedSetTail.add(i)
        }
        val ktBinaryTreeTail = scapeGoatTree.tailSet(toElement)
        val subsetKtBinaryTree = scapeGoatTree.subSet(fromElement, toElement)
        val headKtBinaryTree = scapeGoatTree.headSet(toElement)

        assertEquals(ktSortedSetHead, headKtBinaryTree)
        assertEquals(ktSortedSetSubset, subsetKtBinaryTree)
        assertEquals(ktBinaryTreeTail, ktBinaryTreeTail)
        assertEquals(ktSortedSetHead.last(), headKtBinaryTree.last())
        assertEquals(ktSortedSetTail.last(), ktBinaryTreeTail.last())
        assertEquals(subsetKtBinaryTree.size, ktSortedSetSubset.size)

        val random = Random()
        for (iteration in 1..100) {
            val list = mutableListOf<Int>()
            for (i in 1..20) {
                list.add(random.nextInt(100))
            }
            val treeSet = sortedSetOf<Int>()
            val sgtSubset = ScapeGoatTree<Int>().subSet(list.min()!!, list.max()!! + 1)
            val test = sgtSubset.iterator().hasNext()
            assertFalse(test, "Iterator of empty set should not have next element")
            for (element in list) {
                treeSet += element
                sgtSubset += element
            }
            val treeIt = treeSet.iterator()
            val binaryIt = sgtSubset.iterator()
            println("Traversing $list")
            while (treeIt.hasNext()) {
                assertEquals(treeIt.next(), binaryIt.next(), "Incorrect iterator state while iterating $treeSet")
            }
            val iterator1 = sgtSubset.iterator()
            val iterator2 = sgtSubset.iterator()
            println("Consistency check for hasNext $list")
            // hasNext call should not affect iterator position
            while (iterator1.hasNext()) {
                assertEquals(
                        iterator2.next(), iterator1.next(),
                        "Call of iterator.hasNext() changes its state while iterating $treeSet"
                )
            }
        }
    }
}