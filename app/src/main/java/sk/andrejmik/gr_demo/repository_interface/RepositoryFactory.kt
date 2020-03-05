package sk.andrejmik.gr_demo.repository_interface

import sk.andrejmik.gr_demo.repository_fake.UserFakeRepository

open class RepositoryFactory
{
    companion object
    {
        /**
         * Change this parameter to change repository type
         */
        private val repositoryType = RepositoryType.FAKE

        fun getUserRepository(): IUserRepository
        {
            return when (repositoryType)
            {
                RepositoryType.FAKE -> UserFakeRepository()
            }
        }

        private enum class RepositoryType
        {
            FAKE
        }
    }
}