package com.mayabansi.webapp.service.spock

import spock.lang.Specification
import spock.lang.Stepwise

@Stepwise
class StepwiseDemo extends Specification {

    def "Step 1"() {
        given: "a simple integer is assigned a value"
            int i =5

        when: "multiply the int"
            i *= 5

        then:
            i == 25
    }

    def "Step 2 - Watch Step 2 will always come after Step 1"() {
        given: "a simple integer is assigned a value"
            int i = 10

        when: "multiply the int"
            i *= 5

        then:
            i == 50
    }

    def "Step 3 will come after Step 2"() {
        given: "a simple integer is assigned a value"
            int i = 25

        when: "multiply the int"
            i *= 5

        then: "check the final int value"
            i == 125
    }

}
